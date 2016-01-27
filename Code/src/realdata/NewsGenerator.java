package realdata;

import java.util.Collection;

import datastructures.News;
public interface NewsGenerator {
	public Collection<News> getNews(int id);
	public Collection<Integer> getAvailableIDs();
}
