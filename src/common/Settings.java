package common;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {
	
	private static final Path HOME_DIR_PATH;

	public static final String SOLVER_TMP_DIR;
	
	public static final String TARGET_CLASS_PATH;
	
	public static final String TARGET_SOURCE_PATH; 
	
	static {
		try {
			/* file = $HOME/bin/common */
			File file = new File(Settings.class.getResource("").toURI());
			for (int jump = 0; jump < 2; ++jump)
				file = file.getParentFile();
			HOME_DIR_PATH = Paths.get(file.getAbsolutePath());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		SOLVER_TMP_DIR = HOME_DIR_PATH.resolve("tmp/z3").toAbsolutePath().toString();
		TARGET_CLASS_PATH = HOME_DIR_PATH.resolve("bin").toAbsolutePath().toString();
		TARGET_SOURCE_PATH = HOME_DIR_PATH.resolve("src").toAbsolutePath().toString();
	}

}
