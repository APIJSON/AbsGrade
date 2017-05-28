/*Copyright ©2017 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package zuo.biao.absgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.istack.internal.NotNull;

/**分级(父子分级)工具类
 * 支持 单层、双层、多层(无限层)
 * @author Lemon
 */
public class AbsGradeUtil {

	/**
	 * @param <T>
	 */
	public interface GradeCallback<T> {
		/**获取对象id
		 * @param data
		 * @return 例如 data.getId();
		 */
		Long getId(@NotNull T data);
		/**获取父对象id
		 * @param data
		 * @return 例如 data.getParentId();
		 */
		Long getParentId(@NotNull T data);
	}

	/**
	 * @param <T>
	 */
	public interface SingleGradeCallback<T> extends GradeCallback<T> {
		/**设置父对象
		 * 应用场景: 评论中 子评论child 回复 父评论parent
		 * @param child
		 * @param parent
		 * @use 例如 child.setParent(parent);
		 */
		void setParent(@NotNull T child, T parent);
	}

	/**
	 * @param <T>
	 */
	public interface MultipleGradeCallback<T> extends GradeCallback<T> {
		/**获取子对象列表
		 * 应用场景: 1.评论data 下展开 子评论列表childList；2.文件夹data 中展开 文件(夹)列表childList
		 * @param data
		 * @return 例如 data.getChildList();
		 */
		List<T> getChildList(@NotNull T data);
		/**设置子对象列表
		 * 应用场景: 1.评论data 下展开 子评论列表childList；2.文件夹data 中展开 文件(夹)列表childList
		 * @param data
		 * @param childList
		 * @use 例如 data.setChildList(childList);
		 */
		void setChildList(@NotNull T data, List<T> childList);
	}

	/**
	 * @param <T>
	 */
	public interface DoubleGradeCallback<T> extends MultipleGradeCallback<T> {
		/**设置父对象
		 * 应用场景: 评论中 子评论child 回复 父评论parent
		 * @param child
		 * @param parent
		 * @use 例如 child.setParent(parent);
		 */
		void setParent(@NotNull T child, T parent);
	}


	
	
	/**转为单层
	 * 适用场景:微信朋友圈单层评论
	 * @param <T>
	 * @param list
	 * @param callback
	 * @return
	 */
	public static <T> List<T> toSingle(List<T> list, @NotNull SingleGradeCallback<T> callback) {
		if (list == null || list.isEmpty()) {
			return list;
		}

		//parent和child分类
		Map<Long, T> parentMap = new LinkedHashMap<Long, T>();//added
		long id;
		long toId;
		for (T item : list) {
			id = item == null ? 0 : callback.getId(item); //item.getId();
			if (id <= 0) {
				continue;
			}
			parentMap.put(id, item);
		}

		//child内设置parent
		T parent;
		for (final T item : new ArrayList<T>(parentMap.values())) {
			parent = null;
			toId = callback.getParentId(item); //item.getToId();
			if (toId > 0) {
				parent = parentMap.get(toId);
				if (parent == null) {
					parentMap.remove(callback.getId(item)); //item.getId());
					continue;
				}
			}
			if (parent != null) {
				callback.setParent(item, parent); //item.setToUser(parent.getUser());
				parentMap.put(callback.getId(item), item); //item.getId(), item);
			}
		}

		return new ArrayList<T>(parentMap.values());
	}



	/**转为双层
	 * 适用场景:QQ空间双层评论，微博双层评论等
	 * @param <T>
	 * @param list
	 * @param callback
	 * @return
	 */
	public static <T> List<T> toDouble(List<T> list, @NotNull DoubleGradeCallback<T> callback) {
		if (list == null || list.isEmpty()) {
			return list;
		}

		//parent和child分类
		Map<Long, T> parentMap = new LinkedHashMap<Long, T>(); //added
		Map<Long, T> allChildMap = new LinkedHashMap<Long, T>();
		long id;
		long toId;
		for (T item : list) {
			id = item == null ? 0 : callback.getId(item); //item.getId();
			if (id <= 0) {
				continue;
			}
			callback.setChildList(item, null); //item.setChildList(null); //避免重复添加child

			toId = callback.getParentId(item); //item.getToId();
			if (toId <= 0) { //parent
				parentMap.put(id, item);
			} else { //child
				allChildMap.put(id, item);
			}
		}

		//child放到parent的childList中
		boolean isFirst;
		T parent;
		List<T> childList;
		for (final T child : allChildMap.values()) {
			toId = callback.getParentId(child); //child.getToId();
			isFirst = true;
			while (parentMap.containsKey(toId) == false) { //根据父评论一步步找到一级父评论
				parent = toId <= 0 ? null : allChildMap.get(toId);
				if (parent == null) {
					break;
				}
				if (isFirst) {
					isFirst = false;
					callback.setParent(child, parent); //child.setToUser(parent.getUser());
				}

				toId = callback.getParentId(parent); //parent.getToId();//父评论的父评论的id
			}

			parent = parentMap.get(toId);
			if (parent == null) {
				continue;
			}
			if (toId == callback.getParentId(child)) { //child.getToId()) {
				callback.setParent(child, parent); //child.setToUser(parent.getUser());
			}

			childList = callback.getChildList(parent); //parent.getChildList();
			if (childList == null) {
				childList = new ArrayList<T>();
			}
			childList.add(child);

			callback.setChildList(parent, childList); //parent.setChildList(childList);
			parentMap.put(toId, parent);
		}

		return new ArrayList<T>(parentMap.values());
	}

	
	
	/**转为多层(无限层级)
	 * 适用场景:文件夹多级文件(夹)，例如系统文件夹和百度网盘
	 * @param <T>
	 * @param list
	 * @param callback
	 * @return
	 */
	public static <T> List<T> toMultiple(List<T> list, @NotNull MultipleGradeCallback<T> callback) {
		if (list == null || list.isEmpty()) {
			return list;
		}

		//把所有同parentId的item放到同一个itemMap<id, item>中
		Map<Long, Map<Long, T>> gradeMap = new LinkedHashMap<Long, Map<Long, T>>(); //added
		Map<Long, T> itemMap;//added
		Long id;
		Long parentId;
		for (T item : list) {
			id = item == null ? null : callback.getId(item); //item.getId();
			if (id == null || id <= 0) {
				continue;
			}
			callback.setChildList(item, null); //item.setChildList(null);//避免重复添加child

			parentId = callback.getParentId(item); //item.getToId();
			if (parentId == null) {
				parentId = new Long(0);
			}

			itemMap = null;
			if (gradeMap.containsKey(parentId)) {
				itemMap = gradeMap.get(parentId);
			}
			if (itemMap == null) {
				itemMap = new HashMap<Long, T>();
			}
			itemMap.put(id, item);
			gradeMap.put(parentId, itemMap);
		}


		//倒序装载child.   gradeMap<pid, itemMap<id, item>>
		List<Long> pIdList = new ArrayList<Long>(gradeMap.keySet());

		Long lPId; //last parent id
		Map<Long, T> lCMap; //last child map
		Long pId; //parent id
		T item;
		for (int i = pIdList.size() - 1; i >= 1; i--) {
			lPId = pIdList.get(i);
			if (lPId == null) {
				lPId = new Long(0);
			}

			//逐层遍历上面的所有itemMap，只要有符合的就break
			for (int j = i - 1; j >= 0; j--) {
				pId = pIdList.get(j);
				if (pId == null) {
					pId = new Long(0);
				}

				itemMap = gradeMap.get(pId);

				if (itemMap.containsKey(lPId)) {

					item = itemMap.get(lPId);
					if (lPId == null || lPId <= 0) {
						itemMap.remove(lPId);
					} else {
						lCMap = gradeMap.get(lPId);
						callback.setChildList(item, lCMap == null ? null : new ArrayList<T>(lCMap.values()));
						itemMap.put(lPId, item);
						gradeMap.put(pId, itemMap);
					}
					gradeMap.remove(lPId); //移除最后一项，只保留顶级map

					break;
				}
			}
		}

		//取出顶级itemMap内的values并转换为List返回
		Map<Long, T> topItemMap = gradeMap.get(new Long(0));
		return topItemMap == null ? null : new ArrayList<T>(topItemMap.values());
	}

}