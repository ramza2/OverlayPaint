package com.ramza.overlaypaint.datas;

import java.util.List;

public interface IDataManager<T> {
	public List<T> getPaintList();
	public T loadData(String id);
	public void saveData(T data);
}
