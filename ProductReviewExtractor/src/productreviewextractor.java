/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;



/**
 *
 * @author developer
 */
public class productreviewextractor {
    static String revlink;
    static String reviewtitle;
        static String reviewcontent;
        static String revstars,revstarword,reviewsrc,pname;
        static String pros;
        static String cons;
        static String strurl,strdbnameip, strdbuser, strdbpass, strdb,strouttablename,strintablename;
        static int docno=0,id=0;
        public productreviewextractor() throws FileNotFoundException {
			// TODO Auto-generated constructor stub
        	Properties props = new Properties();
    	    Reader read=(Reader)new FileReader(new File("settings.properties"));
    	try{
    		
    		System.out.println("start1");
    		props.load(read);
    	}catch(Exception e)
    	{
    	}
    	try{
    		strdbnameip=props.getProperty("DataBaseIP");
    		   strdbuser=props.getProperty("DataBaseUser");
    		   strdbpass=props.getProperty("DataBasePassword");
    		   strdb=props.getProperty("DataBase");
    		   strouttablename=props.getProperty("OutputTableName");
    		   strintablename=props.getProperty("InputTableName");
    		   
    	   read.close();
    	   
    	   
    	  
    	}catch(Exception cnfe){
    		System.out.println("Error..."+cnfe);
    	}
		}
     public static void main(String[] args) throws FileNotFoundException {
    	 @SuppressWarnings("unused")
		productreviewextractor pd=new productreviewextractor();
         Connection connection;
	PreparedStatement stmt=null,stmtupdate=null;
        
        
        List<String> reviewlinks = new ArrayList<String>();
        
         try{
             DBActivityMInsLM db=new DBActivityMInsLM(strdbnameip,strdbuser,strdbpass,strdb);
             Class.forName("com.mysql.jdbc.Driver");						
		connection=DriverManager.getConnection("jdbc:mysql://"+strdbnameip+":3306/"+strdb+"",""+strdbuser+"",""+strdbpass+"");
		
                stmt = connection.prepareStatement("SELECT * FROM "+strintablename+" where Flag=0");

                 ResultSet rs = stmt.executeQuery();
             WebDriver driver=new FirefoxDriver();     
             driver.get("http://www.google.com/");
             while (rs.next()){
              revlink = rs.getString("Link");
              docno=rs.getInt("Docno");
              id=rs.getInt("ID");
             reviewlinks.add(revlink);
             System.out.println(docno);
                   driver.navigate().to(revlink);
             reviewsrc=driver.getPageSource().toString();
             //System.out.println(reviewsrc);
             
             Pattern pregex = Pattern.compile("<span class=\"fn\">(.*?)</span>",
 		Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
 	Matcher pregexMatcher = pregex.matcher(reviewsrc);
 	if (pregexMatcher.find()) {
 		pname=pregexMatcher.group(1);
 	} 
         Pattern titleregex = Pattern.compile("<div class=\\\"review\\\">\\s*<h3>(.*?)</h3>",
 		Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
 	Matcher titleregexMatcher = titleregex.matcher(reviewsrc);
 	if (titleregexMatcher.find()) {
 		reviewtitle= titleregexMatcher.group(1).replaceAll("<.*?>", "").trim();
 	} 
             
             
             Pattern revregex = Pattern.compile("<div class=\\\"rating-small\\\">.*?title=\\\"(.*?)\\\".*?>.*?</div>.*?<p> \\-(.*?)</p>.*?class=\"review-content\">(.*?)</div>",
 		Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
 				
             Matcher revregexMatcher = revregex.matcher(reviewsrc);
 				
             Pattern prosregex = Pattern.compile("class=\\\"pros\\\">.*?\\\"icon-plus\\\"></i>(.*?)</p>",
 						Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
 					Matcher prosregexMatcher = prosregex.matcher(reviewsrc);
 					Pattern consregex = Pattern.compile("<p class=\\\"cons\\\">.*?\\\"icon-minus\\\"></i>(.*?)</p>",
 							Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
 						Matcher consregexMatcher = consregex.matcher(reviewsrc);
 					
 				if(revregexMatcher.find()) {
 						
 						
 						reviewcontent = revregexMatcher.group(3).replaceAll("<.*?>", "").trim().replaceAll("(\\s+\\s+\\s+)", "");
 						revstars= revregexMatcher.group(1).trim();
                                                 revstarword=revregexMatcher.group(2).trim();
 							if (prosregexMatcher.find()) {
 								pros=prosregexMatcher.group(1).replaceAll("<.*?>", "").replace("&amp;", "").trim(); 
 							} else pros=null;
 								if (consregexMatcher.find()) {
 									cons=consregexMatcher.group(1).replaceAll("<.*?>", "").replace("&amp;", "").trim();
 								} else cons=null;                                 
                                 }
                                 System.out.println(reviewtitle+"\t\n"+reviewcontent+"\n\t"+pros+cons+revstarword);
               
               System.out.println(revlink);        
                                 db.insertreview(docno, reviewtitle, reviewcontent, pname, pros, cons, revlink, revstars, revstarword,strouttablename);
                                 
             stmtupdate=connection.prepareStatement("update "+strintablename+" set Flag=1 where ID="+id);
             stmtupdate.executeUpdate();
             }
                 
                 stmt.close();
               stmtupdate.close();
              
          }catch(Exception e)
          {
              System.out.println(e);
          }
      }
     
 }

