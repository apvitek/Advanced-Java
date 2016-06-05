package adapter;

import enums.FileType;

public interface CreateAuto {
	public void buildAuto(String fileName, FileType type);
	public void printAuto(String modelName);
}
