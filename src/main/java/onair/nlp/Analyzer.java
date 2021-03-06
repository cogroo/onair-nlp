package onair.nlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import opennlp.tools.cmdline.PerformanceMonitor;

import org.cogroo.analyzer.AnalyzerI;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.text.impl.DocumentImpl;
import org.cogroo.text.impl.SentenceImpl;

import com.google.common.io.Closeables;
import com.google.common.io.Files;

public class Analyzer {

  private static void help() {
    StringBuilder sb = new StringBuilder();
    sb.append("Usage: java -jar onair-nlp[version].jar lang inputFile outputFile encoding\n");
    sb.append("  supported languages: pt en\n");

    System.out.println(sb);
  }

  public static void main(String[] args) {

    // lets do some checking

    if (args == null || args.length != 4) {
      help();
      return;
    }

    String language = args[0];
    if (!("pt".equals(language) || "en".equals(language))) {
      System.out.println("Invalid language: " + language);
      help();
      return;
    }

    File inFile = new File(args[1]);
    if (!inFile.exists()) {
      System.out.println("Input file does not exists: " + inFile);
      help();
      return;
    }

    File outFile = new File(args[2]);

    Charset charset = null;
    try {
      charset = Charset.forName(args[3]);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Invalid charset encoding: " + charset);
      help();
      return;
    }

    // looks nice! now we can start cogroo with the language

    AnalyzerI cogrooAnalyzer = createCogroo(language);

    // and open the input/output files

    BufferedReader input = null;
    BufferedWriter output = null;

    try {
      
      input = Files.newReader(inFile, charset);
      output = Files.newWriter(outFile, charset);
      Scanner scanner = new Scanner(input);

      final PerformanceMonitor monitor = new PerformanceMonitor("sent");
      monitor.startAndPrintThroughput();

      while (scanner.hasNextLine()) {
        // text.append(scanner.nextLine() + NL);
        String text = scanner.nextLine();
        Document document = new DocumentImpl();
        document.setText(text);
        Sentence sent = new SentenceImpl(0, text.length(), document);
        document.setSentences(Collections.singletonList(sent));

        cogrooAnalyzer.analyze(document);
        print(document, output);
        
        monitor.incrementCounter();
      }
      
      monitor.stopAndPrintFinalResult();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      Closeables.closeQuietly(input);
      Closeables.closeQuietly(output);
    }
  }

  private static AnalyzerI createCogroo(String language) {
    // create cogroo pipe
    String config = null;
    if ("pt".equals(language)) {
      config = "/models_onair_pt_BR.xml";
    } else {
      config = "/models_onair_en.xml";
    }

    ComponentFactory factory = ComponentFactory.create(Analyzer.class
        .getResourceAsStream(config));
    AnalyzerI cogroo = factory.createPipe();

    return cogroo;
  }

  /**
   * A utility method that prints the analyzed document to a writer
   * 
   * @throws IOException
   */
  private static void print(Document document, Writer output)
      throws IOException {
    // and now we navigate the document to print its data
    for (Sentence sentence : document.getSentences()) {

      for (Token token : sentence.getTokens()) {
        // F == null ? other_F : F
        
        String lexeme = token.getLexeme();
        
        String[] lemmaArr = token.getLemmas();
        String lemmas;
        if (lemmaArr == null || token.getLemmas().length == 0) {
          lemmas = "-";
        } else {
          lemmas = Arrays.toString(token.getLemmas());
        }
        String pos = token.getPOSTag();
        String feat = token.getFeatures();
        String chunk = token.getChunkTag();

        if (feat == null)
          feat = "-";
        
        // check if it is a MWE
        boolean isMWE = false;
        if(lexeme.contains("_")) {
          String coverred = sentence.getText().substring(token.getStart(), token.getEnd());
          if(coverred.contains(" ")) {
            isMWE = true;
            
            // we handle it here...
            String[] parts = coverred.split("\\s+");
            String tag = "B-" + pos; 
            
            for (String tok : parts) {
              String out = String.format("%-15s %-12s %-6s %-10s %-6s\n", tok,
                  "[" + tok + "]", tag, feat, chunk);
              output.append(out);
              
              tag = "I-" + pos;
              chunk = chunk.replace("B-", "I-");
            }
          }
        }
        
        if(!isMWE) {
          String out = String.format("%-15s %-12s %-6s %-10s %-6s\n", lexeme,
              lemmas, pos, feat, chunk);
          output.append(out);
        }
      }

      output.append("\n");

    }

  }

}
