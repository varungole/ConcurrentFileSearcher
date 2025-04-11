package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BigFileGenerator {

   public static void createBigFile() {
       String fileName = "src/main/java/org/example/bigFile.txt";
       int totalLines = 5_000_000;
       int imhotepLine = 2_999_999;

       try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
           for(int i =1; i<=totalLines; i++) {
               if(i == imhotepLine) {
                   writer.write("this line contains imhotep\n");
               } else {
                   writer.write("This is another boring line\n");
               }
           }
       } catch (IOException e) {
           System.out.println("Error creating bigFile");
       }

       System.out.println("✅ Generated big file with " + totalLines + " lines. 'imhotep' placed at line " + imhotepLine);

   }
}