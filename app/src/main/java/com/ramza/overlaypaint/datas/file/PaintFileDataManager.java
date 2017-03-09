package com.ramza.overlaypaint.datas.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ramza.overlaypaint.datas.IDataManager;
import com.ramza.overlaypaint.datas.PaintData;

import android.os.Environment;

public class PaintFileDataManager implements IDataManager<PaintData> {
	private final File EXTERNAL_DIR = Environment.getExternalStorageDirectory();
	private final String DIR_NAME = "OverlayPaint";
	private final String FILE_EXT = "paint";

	private File fileDir;

	private Map<String, PaintData> paintMap;

	public PaintFileDataManager() {
		// TODO Auto-generated constructor stub
		fileDir = new File(EXTERNAL_DIR, DIR_NAME);
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}

		paintMap = new HashMap<String, PaintData>();

		LoadPaintDatas();
	}

	private void LoadPaintDatas() {
		File[] files = fileDir.listFiles();
		if(files != null){
			int length = files.length;
			for (int i = 0; i < length; i++) {
				File file = files[i];
				if (getExtension(file.getName()).equals(FILE_EXT)) {
					BufferedReader bufferedReader = null;
					try {
						PaintData paintData = new PaintData();
						paintData.id = file.getName();

						bufferedReader = new BufferedReader(new FileReader(file));
						StringBuffer sb = new StringBuffer();
						String line = null;
						while ((line = bufferedReader.readLine()) != null) {
							sb.append(line);
						}
						paintData.jsonData = sb.toString();
						paintMap.put(paintData.id, paintData);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						try {
							if (bufferedReader != null)
								bufferedReader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	@Override
	public List<PaintData> getPaintList() {
		// TODO Auto-generated method stub
		List<PaintData> paintList = new ArrayList<PaintData>(paintMap.values());
		return paintList;
	}

	public static String getExtension(String fileStr) {
		return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
	}

	@Override
	public PaintData loadData(String id) {
		// TODO Auto-generated method stub
		return paintMap.get(id);
	}

	@Override
	public void saveData(PaintData data) {
		// TODO Auto-generated method stub
		BufferedWriter bufferedWriter = null;
		try {
			File file = new File(fileDir, data.id);
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			bufferedWriter.write(data.jsonData);
			bufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
