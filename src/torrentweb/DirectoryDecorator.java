package torrentweb;

import java.io.*;

public class DirectoryDecorator {
	File directory;

	public DirectoryDecorator(File directory) {
		this.directory = directory;
	}

	public long lastModified() {
		long latestModified = directory.lastModified();
		File[] content = directory.listFiles();
		for (int i = 0; i < content.length; i++) {
		long modifiedDate;
                    if (content[i].isDirectory()) {

				modifiedDate = new DirectoryDecorator(content[i])
						.lastModified();
                        } else {
                        modifiedDate = content[i].lastModified();
                        }
				if (modifiedDate > latestModified) {
					latestModified = modifiedDate;
				}
			
		}
		return latestModified;
	}

}
