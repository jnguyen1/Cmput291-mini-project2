

import java.util.*;

//need to create a function to handle %

	
	public class query {
	static Vector<scannedWords> text= new Vector<scannedWords>();
	static Vector<scannedWords> con= new Vector<scannedWords>();
	static Vector<scannedWords> title= new Vector<scannedWords>();
	static Vector<scannedWords> doc= new Vector<scannedWords>();
	static Vector<result> resultSet = new Vector<result>();
	static int searches = 0;
	
	@SuppressWarnings("null")
	public static void main(String args[]){
		
		String input = readEntry("Entery Query:");
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
			if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("doc") == 0 ){
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
		for (int i = 0; i < len; i++){
			if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("tx") == 0 ){
				scannedWords temp = new scannedWords();
				if((i+1) < (len -1)){
				temp.sting = (tokens[i+1].substring(0, (tokens[i+1]).length()-3));
				text.add(temp);
				searches++;
				}
				if((i + 1) == (len -1)){
					temp.sting = (tokens[i+1]);
					text.add(temp);
					searches++;
			}
		}
		}
			
		for (int i = 0; i < len; i++){
			if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("ti") == 0 ){
				scannedWords temp = new scannedWords();
				if((i+1) < (len -1)){
				temp.sting = (tokens[i+1].substring(0, (tokens[i+1]).length()-3));
				title.add(temp);
				searches++;
				}
				if((i + 1) == (len -1)){
					temp.sting = (tokens[i+1]);
					title.add(temp);
					searches++;
			}
		}
		}
			
		for (int i = 0; i < len; i++){
			if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("doc") == 0 ){
				scannedWords temp = new scannedWords();
				if((i+1) < (len -1)){
				temp.sting = (tokens[i+1].substring(0, (tokens[i+1]).length()-3));
				doc.add(temp);
				searches++;
				}
				if((i + 1) == (len -1)){
					temp.sting = (tokens[i+1]);
					doc.add(temp);
					searches++;
			}
		}
		}
		
		for (int i = 0; i < len; i++){
			if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("co") == 0 ){
				scannedWords temp = new scannedWords();
				if((i+1) < (len -1)){
				temp.sting = (tokens[i+1].substring(0, (tokens[i+1]).length()-3));
				con.add(temp);
				searches++;
				}
				if((i + 1) == (len -1)){
					temp.sting = (tokens[i+1]);
					con.add(temp);
					searches++;
			}
		}
		}

			
		//co: fungiii ti:the query is: strange co: fungi
		
	
		//debug, print out all values added to the vectors
		/*
	    int size = text.size();
		while (size > 0){
			System.out.println("text to be searched: " + (text.get(size-1)).sting); //debug
			size--;
		}
	    size = con.size();
		while (size > 0){
			System.out.println("con to be searched: " + (con.get(size-1)).sting); //debug
			size--;
		}
	    size = title.size();
		while (size > 0){
			System.out.println("title to be searched: " + (title.get(size-1)).sting); //debug
			size--;
		}
	    size = doc.size();
	    while (size > 0){
				System.out.println("doc to be searched: " + (doc.get(size-1)).sting); //debug
				size--;
			}
	*/
	  

		// how do i search on multiple conditions
		//do i just print out the results or do i store them for later
		// -- should I store results in a string array then after querying all the databases
		// return only the results that are in all of the result set?
	  
		// do i have to format wild card searches differently

		//code for handling % case
		if(text.size()>0){
		for(int size = (text.size()-1); size >= 0; size--){
		String search = (text.get(size)).sting;
    
    	if(search.substring(0,1).equals("%")){
    		search = "*" + search.substring(1, (search.length())); 
    		text.get(size).sting = search;
	    }
    	
    	if( search.substring((search.length() -1), search.length()).equals("%")){
    		search = search.substring(0,(search.length() -1)) + "*"; 
    		text.get(size).sting = search;
    		}
		}
		}
		
		//code for handling % case
		if(doc.size()>0){
		for(int size = (doc.size()-1); size >= 0; size--){
		String search = (doc.get(size)).sting;
    
    	if(search.substring(0,1).equals("%")){
    		search = "*" + search.substring(1, (search.length())); 
    		doc.get(size).sting = search;
	    }
    	
    	if( search.substring((search.length() -1), search.length()).equals("%")){
    		search = search.substring(0,(search.length() -1)) + "*"; 
    		doc.get(size).sting = search;
    		}
		}
		}
		
		//code for handling % case
		if(con.size()>0){
		for(int size = (con.size()-1); size >= 0; size--){
		String search = (con.get(size)).sting;
    
    	if(search.substring(0,1).equals("%")){
    		search = "*" + search.substring(1, (search.length())); 
    		con.get(size).sting = search;
	    }
    	
    	if( search.substring((search.length() -1), search.length()).equals("%")){
    		search = search.substring(0,(search.length() -1)) + "*"; 
    		con.get(size).sting = search;
    		}
		}
		}
		

		//code for handling % case
		if(title.size()>0){
		for(int size = (title.size()-1); size >= 0; size--){
		String search = (title.get(size)).sting;
    
    	if(search.substring(0,1).equals("%")){
    		search = "*" + search.substring(1, (search.length())); 
    		title.get(size).sting = search;
	    }
    	
    	if( search.substring((search.length() -1), search.length()).equals("%")){
    		search = search.substring(0,(search.length() -1)) + "*"; 
    		title.get(size).sting = search;
    		}
		}
		}
		
		
//database searches		
	try {
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setErrorStream(System.err);
		dbConfig.setErrorPrefix("MyDbs");
			
			
	    //databases
		Database tx_db = new Database("tx.idx", null, dbConfig);
		Database co_db = new Database("co.idx", null, dbConfig);
		Database doc_db = new Database("doc.idx", null, dbConfig);
		Database ti_db = new Database("ti.idx", null, dbConfig); 
	   
	    DatabaseEntry key = null, data = null;
		
	    //create a cursor
	    Cursor tx_cur = tx_db.openCursor(null, null);
	    Cursor co_cur = co_db.openCursor(null, null);    
	    Cursor doc_cur = doc_db.openCursor(null, null);
	    Cursor ti_cur = ti_db.openCursor(null, null);    
		
	    //get out values of text to be searched
	    
	    
	    for(int size = text.size(); size >= 0; size--){
	    	
	    	String search = (text.get(size-1)).sting;
	    	System.out.println("text to be searched" + (text.get(size-1)).sting); //debug
	    	
	    	//check if the text's first character is % tehn replace it with *
	    	if(search.substring(0,1) == "%"){
	    		
	    		
	    		
	    	}
	    	
	    	
	    	
	    	key.setData(((text.get(size-1)).sting).getBytes());
	    	key.setSize(((text.get(size-1)).sting).length());
	    	
	    	//cursor search, works for multiple results
	    	OperationStatus oprStatus;
	    	oprStatus = tx_cur.getSearchKey(key, data, LockMode.DEFAULT);
	    	
	    	while(oprStatus == OperationStatus.SUCCESS)
	    	{
	    		String result = new String(key.getData());
	    		
	    		System.out.println("result =" + result);  //debug
	    		boolean m = false;
	    		//see if the result is in the result set already 
	    		for(int t= (resultSet.size()-1); t>=0; t--){
	    			if(result ==  resultSet.get(t).sting){
	    				resultSet.get(t).hits++;
	    				m = true;
	    				}
	    		}
	    			
	    		if(m == false){
	    	    		//if not add the result
	    	    		result results = new result();
	    	    		results.sting = result;
	    	    		results.hits = 1;
	    			}
	    		oprStatus = tx_cur.getNextDup(key, data, LockMode.DEFAULT);
	    	}
	    } 
	    tx_db.close();
	    
	    
	    //get out values of title to be searched
	    for(int size = title.size(); size >= 0; size--){
	    	System.out.println("title to be searched" + (title.get(size-1)).sting); //debug
	    	
	    	key.setData(((title.get(size-1)).sting).getBytes());
	    	key.setSize(((title.get(size-1)).sting).length());
	    	
	    	//cursor search, works for multiple results
	    	OperationStatus oprStatus;
	    	oprStatus = ti_cur.getSearchKey(key, data, LockMode.DEFAULT);
	    	while(oprStatus == OperationStatus.SUCCESS)
	    	{
	    		String result = new String(key.getData());
	    		
	    		System.out.println("result =" + result);  //debug
	    		boolean m = false;
	    		//see if the result is in the result set already 
	    		for(int t= (resultSet.size()-1); t>=0; t--){
	    			if(result ==  resultSet.get(t).sting){
	    				resultSet.get(t).hits++;
	    				m = true;
	    				}
	    		}

	    		if(m == false){
	    	    		//if not add the result
	    	    		result results = new result();
	    	    		results.sting = result;
	    	    		results.hits = 1;
	    			}
	    		oprStatus = ti_cur.getNextDup(key, data, LockMode.DEFAULT);
	    	}
	    
	    } 
	    ti_db.close();
	    
	    
	    //get out values of title to be searched
	    for(int size = con.size(); size >= 0; size--){
	    	System.out.println("con to be searched" + (con.get(size-1)).sting); //debug
	    	
	    	key.setData(((con.get(size-1)).sting).getBytes());
	    	key.setSize(((con.get(size-1)).sting).length());
	    	
	    	//cursor search, works for multiple results
	    	OperationStatus oprStatus;
	    	oprStatus = co_cur.getSearchKey(key, data, LockMode.DEFAULT);
	    	while(oprStatus == OperationStatus.SUCCESS)
	    	{

	    		String result = new String(key.getData());
	    		
	    		System.out.println("result =" + result);  //debug
	    		boolean m = false;
	    		//see if the result is in the result set already 
	    		for(int t= (resultSet.size()-1); t>=0; t--){
	    			if(result ==  resultSet.get(t).sting){
	    				resultSet.get(t).hits++;
	    				m = true;
	    				}
	    		}
	    			
	    		if(m == false){
	    	    		//if not add the result
	    	    		result results = new result();
	    	    		results.sting = result;
	    	    		results.hits = 1;
	    			}
	    		oprStatus = co_cur.getNextDup(key, data, LockMode.DEFAULT);
	    	}	
	    		
	    } 
	    co_db.close();
	    
	    for(int size = doc.size(); size >= 0; size--){
	    	System.out.println("doc to be searched" + (doc.get(size-1)).sting); //debug
	    	
	    	key.setData(((doc.get(size-1)).sting).getBytes());
	    	key.setSize(((doc.get(size-1)).sting).length());
	    	
	    	//cursor search, works for multiple results
	    	OperationStatus oprStatus;
	    	oprStatus = doc_cur.getSearchKey(key, data, LockMode.DEFAULT);
	    	while(oprStatus == OperationStatus.SUCCESS)
	    	{

	    		
	    		String result = new String(key.getData());
	    		
	    		System.out.println("result =" + result);  //debug
	    		boolean m = false;
	    		//see if the result is in the result set already 
	    		for(int t= (resultSet.size()-1); t>=0; t--){
	    			if(result ==  resultSet.get(t).sting){
	    				resultSet.get(t).hits++;
	    				m = true;
	    				}
	    		}
	    		if(m == false){
	    	    		//if not add the result
	    	    		result results = new result();
	    	    		results.sting = result;
	    	    		results.hits = 1;
	    			}
	    		oprStatus = doc_cur.getNextDup(key, data, LockMode.DEFAULT);
	    	}
	    		
	    } 
	    doc_db.close();
	    
		} catch (Exception ex) 
     { ex.getMessage();} 
		
	//see results that have hits that matches searches
        if (resultSet.size() > 0){
          System.out,println("preloop");
		for(int f =(resultSet.size() -1); f>=0; f--){

                  System.out,println("inloop");
                  
			System.out.println("result at f hits"+resultSet.get(f).hits
					+"sting:" + resultSet.get(f).hits);//debug
			if(resultSet.get(f).hits == searches){
				System.out.println("result:"+ resultSet.get(f).sting); //debug
				
				//from this get the the id and the title of matching pages
                            ;
				
			}

		}
                
        }
        else
        { System.out.println("No results");}
		

}
	
	
	
	
	
	
	
	static String readEntry(String prompt) { 
			 try { 
			     StringBuffer buffer = new StringBuffer(); 
			     System.out.print(prompt); 
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
}


//to compile
//
