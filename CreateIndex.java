import com.sleepycat.db.*;
import java.io.*;

public class CreateIndex
{
	private static String helpMessage = "usage:java CreateIndex DATAFILE TITLEFILE USERFILE TEXTFILE";
	public static void main(String[] args)
	{
		String dataFile = null;
		String invertedTitleFile = null;
		String invertedContributorFile = null;
		String invertedTextFile = null;

		// Require 4 files as arguments.
		if (args.length != 4)
		{
			System.out.println(helpMessage);
			System.exit(1);
		}
		else
		{
			dataFile = args[0];
			invertedTitleFile = args[1];
			invertedContributorFile = args[2];
			invertedTextFile = args[3];
		}

		// Create the BTree index files (title, contributor, text).
		CreateIndex.createBtreeIndex(invertedTitleFile, "ti.idx");	
		CreateIndex.createBtreeIndex(invertedContributorFile, "co.idx");	
		CreateIndex.createBtreeIndex(invertedTextFile, "tx.idx");	
	}

	private static void createBtreeIndex(String inputFile, String indexFile)
	{
		Database db = null;
		DatabaseConfig dbConfig;

		OperationStatus oprStatus;
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();

		BufferedReader file;
		String title = null;
		String documentId = null;

		// Configure the database to be btree and to allow creation of file.
		dbConfig = new DatabaseConfig();
		dbConfig.setType(DatabaseType.BTREE);
		dbConfig.setAllowCreate(true);

		// Open database file.
		try
		{
			db = new Database(indexFile, null, dbConfig);
		}
		catch (DatabaseException e)
		{
			System.out.println("Weird database exception." + e.getMessage());
			return;
		}
		catch (FileNotFoundException e)
		{
			// Shouldn't be a problem because a new file will be created.
			System.out.println("Could not open database file." + e.getMessage());
			return;
		}

		// Open inverted title file for reading.
		try
		{
			file = new BufferedReader(new FileReader(inputFile));

			// Read through the entire file.
			title = file.readLine();
			while (title != null)
			{
				documentId = file.readLine();

				// Fill in key and data pair.
				data.setData(documentId.getBytes());
				data.setSize(documentId.length());
				key.setData(title.getBytes());
				key.setSize(title.length());

				try
				{
					oprStatus = db.put(null, key, data);
					if (oprStatus != OperationStatus.SUCCESS)
					{
						throw new IOException("Invalid key/data pair in file.");
					}
				}
				catch (DatabaseException e)
				{
					System.out.println("Could not add index entry." + e.getMessage());
				}

				title = file.readLine();
			}
		}
		catch (IOException e)
		{
			System.out.println("Could not open file " + inputFile + ". " + e.getMessage());
			return;
		}

		// Close database after it's been populated.
		try
		{
			db.close();
		}
		catch (DatabaseException e)
		{
			System.out.println("Could not close title database. " + e.getMessage());
		}
	}
}
