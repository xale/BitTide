package tracker;

class UserRecordComparator implements Comparator<UserRecord>
{
	private String filename;
	UserRecordComparator(String filename)
	{
		this.filename = filename;
	}
	int compare(UserRecord u1, UserRecord u2)
	{
		return u2.getFileBitmap(filename).getNumberOfBlocks() - u1.getFileBitmap(filename).getNumberOfBlocks();
	}
}
