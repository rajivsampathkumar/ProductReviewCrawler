

/*

*/
import java.sql.*;

public class DBActivityMInsLM{

	Connection connection;
	//Connection mdbcon;
	boolean isCon;
	PreparedStatement stmt=null,stmt2=null,stmt3=null;
	DBActivityMInsLM(String dbname,String user,String pass,String db){
	    try{
	    	
	    	//MS SQL SERVER 
	    	//Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
	    	//connection = DriverManager.getConnection("jdbc:microsoft:sqlserver://"+dbname+":1433/"+db+"",""+user+"",""+pass+"");

	    	//MySQL 
		Class.forName("com.mysql.jdbc.Driver");						
		connection=DriverManager.getConnection("jdbc:mysql://"+dbname+":3306/"+db+"",""+user+"",""+pass+"");
		isCon=true;
                System.out.println("connected");
		}catch(ClassNotFoundException cnfe){
			System.out.println("Error..."+cnfe);
		}
		catch(SQLException sqle){
			System.out.println("SQL Error..."+sqle);
		}
	}
        
	void closeDB()throws Exception{
	        connection.close();
	}
	
	void insertreview(int docno,String revtitle ,String revcontent,String pname,String pros,String cons,String rlink,String revstar,String starword,String table)
	{
	try{
		stmt2=connection.prepareStatement("insert into "+table+"(docno,productname,reviewtitle,reviewcontent,pros,cons,reviewlink,reviewstar,reviewstarword) values(?,?,?,?,?,?,?,?,?)");
		stmt2.setLong(1,docno);
		stmt2.setString(2,pname);
		stmt2.setString(3,revtitle);
		stmt2.setString(4,revcontent);
		stmt2.setString(5,pros);
		stmt2.setString(6,cons);
		stmt2.setString(7,rlink);
		stmt2.setString(8,revstar);
                stmt2.setString(9,starword);
        stmt2.executeUpdate();
        stmt2.close();
	}catch(Exception e){
	System.out.println("Error while inserting reviews..."+e);	
	}
	}
   	
}
