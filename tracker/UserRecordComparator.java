package tracker;
import java.util.Comparator;

class UserRecordComparator implements Comparator<UserRecord>
{
	private String filename;
	public UserRecordComparator(String filename)
	{
		this.filename = filename;
	}
	public int compare(UserRecord u1, UserRecord u2)
	{
		return u2.getFileBitmap(filename).getNumberOfBlocks() - u1.getFileBitmap(filename).getNumberOfBlocks();
	}
}
