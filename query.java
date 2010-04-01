import java.util.*;
import java.util.*;
import java.io.*;
import java.lang.String;
import com.sleepycat.db.*;
import java.lang.Object;

	
	public class query {
	static Vector<String> text= new Vector<String>();
	static Vector<String> con= new Vector<String>();
	static Vector<String> title= new Vector<String>();
	static Vector<result> resultSet = new Vector<result>();
	static int searches = 0;

        static DatabaseConfig dbConfig = new DatabaseConfig();
        static DatabaseEntry key = new DatabaseEntry();
        static DatabaseEntry data = new DatabaseEntry();
        
	@SuppressWarnings("null")
	public static void main(String args[]){
		String input = readEntry("Enter Query:");
		String[] tokens = input.split(":");	

		//fixes the problem of extra colons not related to a search
		int len = tokens.length;
		for (int i = 0; (i+1) < len; i++){
			boolean found = false;
			if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("tx") == 0 ){
				found =true;
			}
			if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("ti") == 0 ){;
				found =true;
			}
			if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("co") == 0 ){
				found =true;
			}
			if (found == false)// then there was a split and : didn't belong to a search term
			{
				//join this sting with the next one, and shuffle down all the other strings
				String temp = tokens[i] + ":" + tokens[i+1];
				tokens[i] = temp;
				for (int j = (i+1); (j+1)< len; j++){
					tokens[j] = tokens[j+1];
				}
				len--;
			}
		}

                
		//split on :
		//read the last 2 characters of the previous string. adds all but the last 2 characters of the next string
		//unless its the last string, then the whole thing is added

                searches += split("tx", text, tokens);
                searches += split("ti", title, tokens);
                searches += split("co", con, tokens);

//database searches		
	try {
          //DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setErrorStream(System.err);
		dbConfig.setErrorPrefix("MyDbs");
			
			
	    //databases
		Database tx_db = new Database("tx.idx", null, dbConfig);
		Database co_db = new Database("co.idx", null, dbConfig);
	        Database ti_db = new Database("ti.idx", null, dbConfig);
      
		
	    //create a cursor
                Cursor tx_cur = tx_db.openCursor(null, null);
                Cursor co_cur = co_db.openCursor(null, null);    
                Cursor ti_cur = ti_db.openCursor(null, null);    
            
	    //get out values of text to be searched
	    
                for(int size = (text.size()-1); size >= 0; size--){
                  String temp = text.get(size);
                  OperationStatus oprStatus;
                
                  //wildcard case
                  if(temp.substring((temp.length() -1), temp.length()).equals("%")){
                    temp = temp.substring(0,(temp.length()-1));   
                    key = new DatabaseEntry();
                    key.setData(temp.getBytes());
              
                    key.setSize(temp.length());
                    
                    data = new DatabaseEntry();
                    oprStatus = tx_cur.getFirst(key, data, LockMode.DEFAULT);
                    while(oprStatus == OperationStatus.SUCCESS)
                  {
                	  String result = new String(data.getData());
                          String word = new String(key.getData());;
                          String[] dreamTheater = result.split(",");
                          int o = 0;
                          while(o< (dreamTheater.length))
                          {
                        	  if(word.startsWith(temp))
                                 {
                            result = dreamTheater[o];
                            boolean m = false;
                	  //see if the result is in the result set already
                            for(int t= (resultSet.size()-1); t>=0; t--){
                            if(result.equals(resultSet.get(t).sting)){
                        	  resultSet.get(t).hits++;
                        	  m = true;
                        	  }
                            }
                            if(m == false){

                		  //if not add the result
                            	result results = new result();
                                        results.sting = result;
                                        results.hits = 1;
                                        resultSet.add(results);
                          }
                        	  	}
                          o++;
                          }
                          data = new DatabaseEntry();
                          oprStatus = tx_cur.getNext(key, data, LockMode.DEFAULT); 
                  }
                  }


                  else{
	    	//cursor search, works for multiple results;
                  key.setData(temp.getBytes());
                  key.setSize(temp.length());
                  oprStatus = tx_cur.getSearchKey(key, data, LockMode.DEFAULT);
                  while(oprStatus == OperationStatus.SUCCESS)
                  {
                	  String result = new String(data.getData());
                          String[] dreamTheater = result.split(",");
                          int o = 0;
                          while(o< (dreamTheater.length))
                          {
                            result = dreamTheater[o];
                	  boolean m = false;
                	  //see if the result is in the result set already
                	  for(int t= (resultSet.size()-1); t>=0; t--){
                            if(result.equals(resultSet.get(t).sting)){
                        	  resultSet.get(t).hits++;
                        	  m = true;
                          }
                	  }
                	  
                	  if(m == false){

                		  //if not add the result
	    		   		result results = new result();
                                        results.sting = result;
                                        results.hits = 1;
                                        resultSet.add(results);
                          }
                          o++;
                          }
                          oprStatus = tx_cur.getNextDup(key, data, LockMode.DEFAULT); 
                      
                  }
                }
                }
                tx_db.close();
               
            
	    //get out values of title to be searched
            for(int size = (title.size()-1); size >= 0; size--){        	 
        	String temp = (title.get(size));
        	OperationStatus oprStatus;
        	//wildcard case
             if(temp.substring((temp.length() -1), temp.length()).equals("%")){
               temp = temp.substring(0,(temp.length()-1));

               key = new DatabaseEntry();
               key.setData(temp.getBytes());
              
              key.setSize(temp.length());

               data = new DatabaseEntry();
   
              oprStatus = ti_cur.getFirst(key, data, LockMode.DEFAULT);
              
              while(oprStatus == OperationStatus.SUCCESS)
            {
          	  String result = new String(data.getData());
                  String word = new String(key.getData());
                    String[] dreamTheater = result.split(",");
                    int o = 0;
                    while(o< (dreamTheater.length))
                    {
                  	  if(word.startsWith(temp))
                  	  {                     	  
                      result = dreamTheater[o];
                      boolean m = false;
          	  //see if the result is in the result set already
                      for(int t= (resultSet.size()-1); t>=0; t--){
                      if(result.equals(resultSet.get(t).sting)){
                  	  resultSet.get(t).hits++;
                  	  m = true;
                  	  }
                      }
                      if(m == false){
          		  //if not add the result
                      	result results = new result();
                                  results.sting = result;
                                  results.hits = 1;
                                  resultSet.add(results);
                    }
                  	  	}
                    o++;
                    }
                    data = new DatabaseEntry();
                    
                    oprStatus = ti_cur.getNext(key, data, LockMode.DEFAULT); 
            }
            }

            else{
            	key.setData(temp.getBytes());
                key.setSize(temp.length());
        	//cursor search, works for multiple results
        	oprStatus = ti_cur.getSearchKey(key, data, LockMode.DEFAULT);
        	while(oprStatus == OperationStatus.SUCCESS)
	    	{
        		String result = new String(data.getData());
                        String[] dreamTheater = result.split(",");
                          int o = 0;
                          while(o< (dreamTheater.length))
                          {

                         result = dreamTheater[o];
                        boolean m = false;
	    		//see if the result is in the result set already 
	    		for(int t= (resultSet.size()-1); t>=0; t--){
                          if(result.equals(resultSet.get(t).sting)){
	    				resultSet.get(t).hits++;
	    				m = true;
	    				}
	    		}
	    		if(m == false){
	    	    		//if not add the result
	    	    		result results = new result();
	    	    		results.sting = result;
	    	    		results.hits = 1;
                                resultSet.add(results);
                                
	    			}
                        o++;
                          }
	    		oprStatus = ti_cur.getNextDup(key, data, LockMode.DEFAULT);
	    	}
	    }
            }

            ti_db.close();
          
	    //get out values of con to be searched
	    for(int size = (con.size()-1); size >= 0; size--){
                String temp = (con.get(size));
            	OperationStatus oprStatus;
            	//wildcard case
            if(temp.substring((temp.length() -1), temp.length()).equals("%")){
               temp = temp.substring(0,(temp.length()-1));
                key = new DatabaseEntry();
                  key.setData(temp.getBytes());
                  key.setSize(temp.length());
                  data = new DatabaseEntry();
                  oprStatus = co_cur.getFirst(key, data, LockMode.DEFAULT);
                  while(oprStatus == OperationStatus.SUCCESS)
                {
              	  String result = new String(data.getData());
                  String word = new String(key.getData());
                        String[] dreamTheater = result.split(",");
                        int o = 0;
                        while(o< (dreamTheater.length))
                        {
                      	  if(word.startsWith(temp))
                      	  {                     	  
                          result = dreamTheater[o];
                          boolean m = false;
              	  //see if the result is in the result set already
                          for(int t= (resultSet.size()-1); t>=0; t--){
                          if(result.equals(resultSet.get(t).sting)){
                      	  resultSet.get(t).hits++;
                      	  m = true;
                      	  }
                          }
                          if(m == false){
              		  //if not add the result
                          	result results = new result();
                                      results.sting = result;
                                      results.hits = 1;
                                      resultSet.add(results);
                        }
                      	  	}
                        o++;
                        }
                        data = new DatabaseEntry();
                        oprStatus = co_cur.getNext(key, data, LockMode.DEFAULT); 
                }
                }
                //non wild card case
                else{
                  key.setData(temp.getBytes());
                key.setSize(temp.length());
	    	//cursor search, works for multiple results
	    	oprStatus = co_cur.getSearchKey(key, data, LockMode.DEFAULT);
	    	while(oprStatus == OperationStatus.SUCCESS)
	    	{

                  String result = new String(data.getData());
                  String[] dreamTheater = result.split(",");
                  int o = 0;
                      
                  while(o< (dreamTheater.length))
                  {
                    result = dreamTheater[o];
                  boolean m = false;
                  //see if the result is in the result set already 
                  for(int t= (resultSet.size()-1); t>=0; t--){
                    if(result.equals(resultSet.get(t).sting)){
                      resultSet.get(t).hits++;
                      m = true;
                    }
                  }
                  
                  if(m == false){
                    //if not add the result
                    result results = new result();
                    results.sting = result;
                    results.hits = 1;
                    resultSet.add(results);
                  }
                  o++;
                  }
                  oprStatus = co_cur.getNextDup(key, data, LockMode.DEFAULT);
	    	}	
                }	
	    } 
	    co_db.close();
           
	//see results that have hits that matches searches
        boolean found = false;
        	if (resultSet.size() > 0){
        		for(int f =(resultSet.size() -1); f>=0; f--){
        			if(resultSet.get(f).hits >= searches){
        				found = true;
        				//from this get the the id and the title of matching pages
        				findMatching(resultSet.get(f).sting);
        			}     
        		}
  
                
        }
                if (found == false)
                { System.out.println("No results");}

                
	} catch (Exception ex) 
          {System.out.println(ex.getMessage());}
}
	

 static void findMatching(String match){
	// find the text where the id is the same as match,  
	//then split the string on | and print the token[1] bit as title and ID
	
	//search the title database for title base on match
	
   System.out.println();
  try {
    Database ti_db = new Database("doc.idx", null, dbConfig);
    DatabaseEntry id = new DatabaseEntry();
    DatabaseEntry title = new DatabaseEntry();
    
    id.setData(match.getBytes());
    id.setSize(match.length());
    
    OperationStatus oprStatus;
    if (ti_db.get(null, id, title, LockMode.DEFAULT) == OperationStatus.SUCCESS)
        {
          String b = new String (title.getData());
          String[] maiden = b.split("|"); //iron
          System.out.println("Id: "+match);
          System.out.print("Title: ");

          int i = 1;
          while (maiden[i].equals("|") == false){
            System.out.print(maiden[i]);
            i++;
          }
          System.out.println();


        }

    ti_db.close(); 
  } catch (Exception ex) 
    { ex.getMessage();}
}
	
	
	
static String readEntry(String prompt) { 
  try { 
    StringBuffer buffer = new StringBuffer(); 
    System.out.println(prompt); 
    System.out.flush(); 
    int c = System.in.read(); 
    while (c!= '\n' && c != -1) { 
      buffer.append((char) c); 
      c = System.in.read(); 
    } 
    return buffer.toString().trim(); 
  } catch (IOException e) { 
    return ""; 
  } 
		
}




static int split(String compare, Vector<String> dest, String[] tokens){
  int len = tokens.length;
  int searches = 0;
  for (int i = 0; i < len; i++){
    if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo(compare) == 0 ){
      String temp = "";
      if((i+1) < (len -1)){
        temp = tokens[i+1].substring(0, (tokens[i+1]).length()-3);
        dest.add(temp);
        searches++;
      }
      if((i + 1) == (len -1)){
        temp = tokens[i+1];
        dest.add(temp);
        searches++;
      }
    }
  }
  return searches;
}






}

