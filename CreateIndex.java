import com.sleepycat.db.*;
import java.io.*;

public class CreateIndex
{
	public static void main(String[] args)
	{
		String dataFile = null;
		String invertedTitleFile = null;
		String invertedContributorFile = null;
		String invertedTextFile = null;

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			System.out.print("Enter name of data file:");
			dataFile= in.readLine();

			System.out.print("Enter name of inverted title file:");
			invertedTitleFile = in.readLine();

			System.out.print("Enter name of inverted contributor file:");
			invertedContributorFile = in.readLine();

			System.out.print("Enter name of inverted text file:");
			invertedTextFile = in.readLine();
		}
		catch (IOException e)
		{
			System.out.println("Could not get one more more required filenames." + e.getMessage());
			System.exit(1);
		}

		try
		{
			CreateIndex.createTitleIndex(invertedTitleFile);	
		}
		catch (IOException e)
		{
			System.out.println("Could not create title index file." + e.getMessage());
		}
	}

	private static void createTitleIndex(String invertedTitleFile) throws IOException
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
			db = new Database("ti.idx", null, dbConfig);
		}
		catch (DatabaseException e)
		{
			System.out.println("Weird database exception." + e.getMessage());
			System.exit(1);
		}
		catch (FileNotFoundException e)
		{
			// Shouldn't be a problem because a new file will be created.
			System.out.println("Could not open database file." + e.getMessage());
			System.exit(1);
		}

		// Open inverted title file for reading.
		file = new BufferedReader(new FileReader(invertedTitleFile));

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
