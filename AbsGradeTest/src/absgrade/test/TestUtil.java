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

package absgrade.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import zuo.biao.absgrade.AbsGradeUtil;
import zuo.biao.absgrade.AbsGradeUtil.DoubleGradeCallback;
import zuo.biao.absgrade.AbsGradeUtil.MultipleGradeCallback;
import zuo.biao.absgrade.AbsGradeUtil.SingleGradeCallback;
import zuo.biao.apijson.JSON;
import zuo.biao.apijson.Log;

/**测试工具类
 * @author Lemon
 */
public class TestUtil implements SingleGradeCallback<Comment>, DoubleGradeCallback<Comment>, MultipleGradeCallback<Comment> {
	private static final String TAG = "TestUtil";

	public static void main(String[] args) {
		Log.d(TAG, "<<<<<<<<<<<<<<<<<<<< AbsGrade test start  >>>>>>>>>>>>>>>>>>>>>>>");
		new TestUtil().test();
		Log.d(TAG, "<<<<<<<<<<<<<<<<<<<< AbsGrade test end >>>>>>>>>>>>>>>>>>>>>>>");
	}

	/**测试
	 */
	public void test() {
		List<Comment> list = TestUtil.newCommentList();
		Log.i(TAG, "before: \n" + JSON.format(list));


		List<Comment> singleList = AbsGradeUtil.toSingle(new ArrayList<Comment>(list), this);
		Log.i(TAG, "\n\n\n\n\n\n toSingle: \n" + JSON.format(singleList));


		List<Comment> doubleList = AbsGradeUtil.toDouble(new ArrayList<Comment>(list), this);
		Log.i(TAG, "\n\n\n\n\n\n toDouble: \n" + JSON.format(doubleList));


		List<Comment> multipleList = AbsGradeUtil.toMultiple(new ArrayList<Comment>(list), this);
		Log.i(TAG, "\n\n\n\n\n\n toMultiple: \n" + JSON.format(multipleList));

	}


	/**生成parentId随机的评论列表
	 * @return
	 */
	public static List<Comment> newCommentList() {
		List<Comment> list = new ArrayList<Comment>();

		Random r = new Random();
		int id;
		for (int i = 0; i < 20; i++) {
			id = i + 1;
			list.add(new Comment().setId(id).setParentId(r.nextInt(id)).setContent("这是评论 " + id));
		}
		return list;
	}


	@Override
	public Long getId(Comment data) {
		return data.getId();
	}

	@Override
	public Long getParentId(Comment data) {
		return data.getParentId();
	}

	@Override
	public void setParent(Comment child, Comment parent) {
		child.setParent(parent);
	}

	@Override
	public List<Comment> getChildList(Comment data) {
		return data.getChildList();
	}

	@Override
	public void setChildList(Comment data, List<Comment> childList) {
		data.setChildList(childList);
	}

}
