package main;

public class SimpleStringTokenizer {
	char delimiter;
	String decorate;
	int indexiterator;
	public SimpleStringTokenizer(String decorate)
	{
	this(decorate,' ');
	}
	public SimpleStringTokenizer(String decorate, char delimiter)
	{
	this.decorate=decorate;
	this.delimiter=delimiter;
	indexiterator=0;
	seekNextTokenStart();
	}
	private void seekNextTokenStart()
	{
		while (indexiterator < decorate.length() && decorate.charAt(indexiterator) == delimiter)
		{
			indexiterator++;
		}
	}
	public String nextToken()
	{
		int startposition = indexiterator;
		while (indexiterator < decorate.length() && decorate.charAt(indexiterator) != delimiter)
		{
			indexiterator++;
		}
		String token=decorate.substring(startposition,indexiterator);
		seekNextTokenStart();
		return token;
	}
	public boolean hasMoreTokens()
	{
		return (indexiterator<decorate.length());
	}
	
	public String remainingString()
	{
		return decorate.substring(indexiterator);
	}
	public int countTokens()
	{
		int initial=indexiterator;
	int count=0;
		while (hasMoreTokens())
	{
		nextToken();
		count++;
	}
	indexiterator=initial;
	return count;
	}
	
}
