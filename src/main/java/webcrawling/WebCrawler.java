package webcrawling;

import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
// web crawling main function where we have 3 methods 
// finding links,write daata files and create files
// entry stage for exeting webcrawl it cantains main methds the crawling sites used
public class WebCrawler {

	// finding links
	/*
	 * input: list of file web element
	 * output: list of string
	 */
    public static List<String> find_Hyper_Links(List<WebElement> lnnks) {
        List<String> list_URLs = new ArrayList<String>();
        for (WebElement Elements : lnnks) {
  
            if (Elements.equals(null))
                continue;
            else {

                list_URLs.add(Elements.getAttribute("href"));
            }
        }
       
        list_URLs.remove(null);
        return (list_URLs);
    }
   // write date to file
    /*
     * input : name file,folder,exe
     * output: void
     */
    public static void content_Write(String namefldr, String conttentt, String file_Nname, String exxttensionn) {
        try {
            File check_fFolderr = new File(namefldr);
            File ff = new File(namefldr + file_Nname + exxttensionn);
            if (!check_fFolderr.exists()) {
               
                boolean creattted = check_fFolderr.mkdirs(); 
                if (creattted) {
                  
                    FileWriter fiWwrriter = new FileWriter(ff, false);
                    fiWwrriter.write(conttentt);
                    fiWwrriter.close();
                } else {
                    System.out.println("Failed to create the folder.");
                }
            } else {
              
                FileWriter fffwWriter = new FileWriter(ff, false);
                fffwWriter.write(conttentt);
                fffwWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurring in file");
        }
    }
    
    
    /*
     * input:urls,file name, folder
     * output: hastable
     */
    public static Hashtable<String, String> createF_ile(String urrl, String cnot, String name_File,String folderrr) {
        Hashtable<String, String> maURL = new Hashtable<String, String>();
        maURL.put(name_File + ".html", urrl);
        content_Write(folderrr, cnot, name_File, ".html");
        return maURL;
    }
}
