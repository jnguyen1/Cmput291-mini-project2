import com.sleepycat.db.*;
import java.io.*;

public Class CreateIndex
{
	private static Database db;
	public static void main(String[] args)
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			System.out.print("Enter name of data file:");
			String dataFile= in.readLine();

			System.out.print("Enter name of inverted title file:");
			String invertedTitleFile = in.readLine();

			System.out.print("Enter name of inverted contributor file:");
			String invertedContributorFile = in.readLine();

			System.out.print("Enter name of inverted text file:");
			String invertedTextFile = in.readLine();
		}
		catch (IOException e)
		{
			System.out.println("Could not get one more more required filenames." + e.getMessage());
			System.exit(1);
		}

		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setType(Database.Type.BTREE);

		CreateIndex.db = new Database("database", null, dbConfig);

		try
		{
			CreateIndex.createTitleIndex(invertedTextFile);	
		}
		catch (IOException e)
		{
			System.out.println("Could not create title index file." + e.getMessage());
			CreateIndex.db.close();
		}
	}

	private static void createTitleIndex(String invertedTitleFile) throws IOException
	{
		OperationStatus oprStatus;
		DatabaseEntry key = null;
		DatabaseEntry data = null;
		String title = null;
		String documentId = null;

		// Open inverted title file for reading.
		BufferedReader file = new BufferedReader(new FileReader(invertedTitleFile));

		// Read through the entire file.
		while (title = file.readLine())
		{
			documentId = file.readLine();

			// Fill in key and data pair.
			data.setData(documentId.getBytes());
			data.setSize(documentId.length());
			key.setData(title.getBytes());
			key.setSize(title.length());

			oprStatus = CreateIndex.db.put(null, key, data);
			if (oprStatus != OperationStatus.SUCCESS)
			{
				throw new IOException("Invalid key/data pair in file.");
			}
		}
	}
}
