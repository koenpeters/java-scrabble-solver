package nl.cubix.scrabble.solver.util;

import java.io.File;
import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * @author Koen Peters, Cubix Concepts
 */
public class ParamValidationUtil {
	
	private ParamValidationUtil() {
		// Cannot be instantiated
	}
	public static void validateParamNotNull(Object param, String paramName) {
		if (param == null) {
			throw new IllegalArgumentException("parameter " + paramName + " cannot be null.");
		}
	}
	public static void validateParamMaxLength(String param, int maxLength, String paramName) {
		if (param.length() > maxLength) {
			throw new IllegalArgumentException("Length of parameter " + paramName + " must be lesser than or equals to " + maxLength);
		}
	}
	public static void validateParamMinLength(String param, int minLength, String paramName) {
		if (param.length() < minLength) {
			throw new IllegalArgumentException("Length of parameter " + paramName + " must be bigger than or equals to " + minLength);
		}
	}
	public static void validateParamMinCollectionSize(Collection param, int minSize, String paramName) {
		if (param.size() < minSize) {
			throw new IllegalArgumentException("Size of Collection parameter " + paramName + " must be bigger than or equals to " + minSize);
		}
	}
	public static void validateParamReadableDirectory(File folder, String paramName) {
		if(!folder.exists() || !folder.canRead() || !folder.isDirectory()) {
			throw new IllegalArgumentException("Folder " + folder.getAbsolutePath() + " in parameter " + paramName + " is not a readble directory.");
		}
	}
	public static String emptyStringToNull(String str) {
		return str == null || str.equals("")? null: str;
	}

	public static String emptyStringToNull(String str, String ifNullValue) {
		String ret = str == null || str.equals("")? null: str;
		return ret == null? ifNullValue: ret;
	}
	
	public static String validateAsADirectory(String absoluteRoot, String dir, boolean createDir) {
		Logger logger	= Logger.getLogger(ParamValidationUtil.class.getName());
		String path		= absoluteRoot + dir;
		
		//1) Check if the directory immedialtly starts with the name and not with \
		// 2) Check if the directory ends with a \ or /
		if (!dir.endsWith("\\") && !dir.endsWith("/")) {
			throw new IllegalArgumentException(dir + " must end with a  \\ or a /.");
		}
		
		// 3) Check if the directory exists if not we may need to create the directory
		File file = new File(path);
		if (!file.exists()) {
			if (createDir) {
				if (!file.mkdir()) {
					throw new RuntimeException("Cannot create new directory: " + path);
				} else {
					logger.info("created directory " + path);
				}
			} else {
				throw new IllegalArgumentException("tempDir " + path + " does not exist");
			}
		}
		
		// 4) Check if the directory is really a directory and not a file or so.
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("tempDir " + path + " is not a directory");
		}	
		
		// 5) All fine.
		return dir;
	}
	
}
