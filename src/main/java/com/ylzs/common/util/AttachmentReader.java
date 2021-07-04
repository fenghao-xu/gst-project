package com.ylzs.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ooxml.extractor.ExtractorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @className AttachmentReader
 * @Description 文档读取工具类
 * @Author sky
 * @create 2020-02-22 19:56:11
 */
public class AttachmentReader {

    private static final String[] WORD= {"doc","docx","xls","xlsx","ppt","pptx"};
    private static final String PDF ="pdf";
    private static final String TXT="txt";

    public static String reader(String path) {
        String text = "";
        String type =  path.substring(path.lastIndexOf(".")+1).toLowerCase();
        try {
            if(TXT.equals(type)) {
                text= txtReader(path);
            } else if(PDF.equals(type)) {
                text = pdfReader(path);
            } else {
                for (int i = 0; i < WORD.length; i++) {
                    if(WORD[i].equals(type)){
                        text = wordReader(path);
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return text;
    }


    public static String wordReader(String path) {
        try {
            return ExtractorFactory.createExtractor(new File(path)).getText();
        } catch (Exception e) {
            System.out.println(path);
            throw new RuntimeException(e);
        }
    }

    public static String txtReader(String path) {

        try {
            File file = new File(path);
            //文本编码探测
            FileCharsetDetector detector = new FileCharsetDetector();
            String charset = detector.guessFileEncoding(file, 2);
            String str = FileUtils.readFileToString(file,charset);

            return str;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public static String pdfReader(String path) {
        String text = "";
        FileInputStream is = null;
        PDDocument document = null;
        try {
            is = new FileInputStream(path);
            PDFParser parser = new PDFParser(new RandomAccessBuffer(is));
            parser.parse();
            document = parser.getPDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if(null!=is){

            }
            try {
                is.close();
            } catch (IOException e) {

            }
        }
        return text;
    }
}
