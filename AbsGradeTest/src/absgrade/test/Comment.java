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

import java.util.List;

/**评论类
 * @author Lemon
 */
public class Comment {

	private long id;
	private long parentId;
	private String content;
	
	private Comment parent;
	private List<Comment> childList;
	
	public long getId() {
		return id;
	}
	public Comment setId(long id) {
		this.id = id;
		return this;
	}
	public long getParentId() {
		return parentId;
	}
	public Comment setParentId(long parentId) {
		this.parentId = parentId;
		return this;
	}
	public String getContent() {
		return content;
	}
	public Comment setContent(String content) {
		this.content = content;
		return this;
	}
	
	
	public Comment getParent() {
		return parent;
	}
	public Comment setParent(Comment parent) {
		this.parent = parent;
		return this;
	}
	public List<Comment> getChildList() {
		return childList;
	}
	public Comment setChildList(List<Comment> childList) {
		this.childList = childList;
		return this;
	}
	
}
