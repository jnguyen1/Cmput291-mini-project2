import com.sleepycat.db.*;
import java.io.*;

public class CreateIndex
{
	private static String helpMessage = "usage:java CreateIndex DATAFILE TITLEFILE USERFILE TEXTFILE";
	public static void main(String[] args)
	{
		String dataFile = null;
		String invertedDataFile = "doc.txt";
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

		CreateIndex.prepareDataFile(dataFile, invertedDataFile);

		// Create the BTree index files (title, contributor, text).
		CreateIndex.createIndex(invertedTitleFile, "ti.idx", DatabaseType.BTREE);	
		CreateIndex.createIndex(invertedContributorFile, "co.idx", DatabaseType.BTREE);	
		CreateIndex.createIndex(invertedTextFile, "tx.idx", DatabaseType.BTREE);	
		CreateIndex.createIndex(invertedDataFile, "doc.idx", DatabaseType.HASH);	
		
		// Remove the invertedDataFile.
		(new File(invertedDataFile)).delete();
	}

	/**
	 * Function:
	 * Converts the data file into a file that has similar format as the inverted files.
	 *
	 * Param:
	 * inputFile - the name of the data file.
	 * outputFile - the name of the inverted file formatted data file.
	 *
	 * Return:
	 * None
	 *
	 * jnguyen1 - 2010-03-28
	 */
	private static void prepareDataFile(String inputFile, String outputFile)
	{
		// Call awk script to do the formatting.
		String oneliner = "awk -f prepareDataFile.awk " + inputFile;

		try{
			Process p = Runtime.getRuntime().exec(oneliner);

			// Read the output from the process and pipe it to the output.
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

			String line;
			while ((line = in.readLine()) != null) 
			{
				out.write(line);
				// Insert the missing newline.
				out.write('\n');
			}
			out.close();
		}
		catch(IOException e)
		{
			System.out.println("Could not run process to convert data file.");
			System.exit(1);
		}
	}

	/**
	 * Function:
	 * Create the database using the inverted file as input.
	 *
	 * Param:
	 * inputFile - the inverted file.
	 * indexFile - the database file.
	 * type - the type of database.
	 *
	 * Return:
	 * None.
	 *
	 * jnguyen1 - 2010-03-26
	 */
	private static void createIndex(String inputFile, String indexFile, DatabaseType type)
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
		dbConfig.setType(type);
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

			// This loop reads two lines from file every iteration.
			// It is assumed that the file follows the format "key\ndata\n"
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
