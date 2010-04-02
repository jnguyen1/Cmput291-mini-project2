import java.util.*;
import java.io.*;
import java.lang.String;
import com.sleepycat.db.*;
import java.lang.Object;

public class query
{
	private static Vector<String> text = new Vector<String>();
	private static Vector<String> con = new Vector<String>();
	private static Vector<String> title = new Vector<String>();
	private static Vector<result> resultSet = new Vector<result>();
	private static int searches = 0;

	private static DatabaseConfig dbConfig = new DatabaseConfig();
	private static DatabaseEntry key = new DatabaseEntry();
	private static DatabaseEntry data = new DatabaseEntry();

	public static void main(String args[])
	{
		String input = readEntry("Enter Query:");
		String[] tokens = input.split(":");	

		//fixes the problem of extra colons not related to a search
		int len = tokens.length;
		for (int i = 0; i < len - 1; i++)
		{
			boolean found = false;
			if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("tx") == 0 )
			{
				found =true;
			}
			else if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("ti") == 0 )
			{
				found =true;
			}
			else if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo("co") == 0 )
			{
				found =true;
			}

			if (found == false)// then there was a split and : didn't belong to a search term
			{
				//join this sting with the next one, and shuffle down all the other strings
				String temp = tokens[i] + ":" + tokens[i+1];
				tokens[i] = temp;
				for (int j = i+1; j < len-1; j++)
				{
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
		searchDB("tx.idx", text);
		searchDB("ti.idx", title);
		searchDB("co.idx", con); 		

		try
		{
			//see results that have hits that matches searches
			boolean found = false;
			if (resultSet.size() > 0)
			{
				for(int f =(resultSet.size() -1); f>=0; f--)
				{
					if(resultSet.get(f).hits >= searches)
					{
						found = true;
						//from this get the the id and the title of matching pages
						findMatching(resultSet.get(f).sting);
					}     
				}
			}
			if (found == false)
			{
				System.out.println("No results");
			}
		}
		catch (Exception ex) 
		{
			System.out.println(ex.getMessage());
		}
	}


	static void findMatching(String match)
	{
		// find the text where the id is the same as match,  
		//then split the string on | and print the token[1] bit as title and ID

		//search the title database for title base on match

		System.out.println();
		try
		{
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
				while (maiden[i].equals("|") == false)
				{
					System.out.print(maiden[i]);
					i++;
				}
				System.out.println();
			}

			ti_db.close(); 
		}
		catch (Exception ex) 
		{
			ex.getMessage();
			System.out.println(ex.getMessage());
		}
	}

	static String readEntry(String prompt)
	{
		try
		{
			StringBuffer buffer = new StringBuffer(); 
			System.out.println(prompt); 
			System.out.flush(); 
			int c = System.in.read(); 
			while (c!= '\n' && c != -1)
			{
				buffer.append((char) c); 
				c = System.in.read(); 
			} 
			return buffer.toString().trim(); 
		}
		catch (IOException e)
		{ 
			return "";
		} 
	}

	/**
	 * Function:
	 * Search the tokens array for string compare. The next element after this token is the
	 * desired search term.
	 *
	 * Param:
	 * compare - the token that represents the type of keyword search (tx, ti, co)
	 * dest - the vector to put the keyword searches that belong to this type of compare.
	 * tokens - the input array of tokens. It is assumed that the next element following a token
	 *		is a string of everything up to and including the next token identifier.
	 *
	 *	Return:
	 *	An integer that represents the number of search terms found.
	 */
	static int split(String compare, Vector<String> dest, String[] tokens)
	{
		int len = tokens.length;
		int searches = 0;
		for (int i = 0; i <(len-1); i++)
		{
			if( (tokens[i].substring((tokens[i].length() - 2), tokens[i].length())).compareTo(compare) == 0 )
			{
				String temp = "";
				if((i+1) < (len -1))
				{
					temp = tokens[i+1].substring(0, (tokens[i+1]).length()-3);
					dest.add(temp);
					searches++;
				}
				if((i + 1) == (len -1))
				{
					temp = tokens[i+1];
					dest.add(temp);
					searches++;
				}
			}
		}

		return searches;
	}

	/**
	 * Function:
	 * Perform the search on a database.
	 *
	 * Param:
	 * DBfile - the filename of the database that the searches will be performed in.
	 * vec - the vector that holds the search terms.
	 *
	 * Return:
	 * None.
	 */
	static void searchDB(String DBfile, Vector<String> vec)
	{
		try
		{
			dbConfig.setErrorStream(System.err);
			dbConfig.setErrorPrefix("MyDbs");
			Database db = new Database(DBfile, null, dbConfig); 
			Cursor cur = db.openCursor(null, null);
			DatabaseEntry key = null;
			DatabaseEntry data = null;
			OperationStatus oprStatus;
			for(int size = (vec.size()-1); size >= 0; size--)
			{
				String temp = vec.get(size);               
				//wildcard case
				if(temp.endsWith("%")){
					if (temp.length() == 1)
					{
						System.out.println("Warning, just '%' entered");
						db.close();
						return;
					}

					temp = temp.substring(0,(temp.length()-1));

					key = new DatabaseEntry();
					key.setData(temp.getBytes());        
					key.setSize(temp.length());

					data = new DatabaseEntry();
					oprStatus = cur.getFirst(key, data, LockMode.DEFAULT);
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
								for(int t= (resultSet.size()-1); t>=0; t--)
								{
									if(result.equals(resultSet.get(t).sting))
									{
										resultSet.get(t).hits++;
										m = true;
									}
								}

								if(m == false)
								{
									//if not add the result
									result results = new result();
									results.sting = result;
									results.hits = 1;
									resultSet.add(results);
								}
							}
							o++;
						}
						key = new DatabaseEntry();
						data = new DatabaseEntry();
						oprStatus = cur.getNext(key, data, LockMode.DEFAULT); 
					}
				}

				else
				{
					//cursor search, works for multiple results;
					key = new DatabaseEntry();
					key.setData(temp.getBytes());
					key.setSize(temp.length());
					data = new DatabaseEntry();
					oprStatus = cur.getSearchKey(key, data, LockMode.DEFAULT);
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
							for(int t= (resultSet.size()-1); t>=0; t--)
							{
								if(result.equals(resultSet.get(t).sting))
								{
									resultSet.get(t).hits++;
									m = true;
								}
							}

							if(m == false)
							{
								//if not add the result
								result results = new result();
								results.sting = result;
								results.hits = 1;
								resultSet.add(results);
							}
							o++;
						}
						oprStatus = cur.getNextDup(key, data, LockMode.DEFAULT);                       
					}
				}
			}
			db.close();
		}
		catch (DatabaseException ex) 
		{
			System.out.println(ex.getMessage());
		}
		catch (IOException ex) 
		{
			System.out.println(ex.getMessage());
		}
	}
}
