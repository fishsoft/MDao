# 手写数据库框架

通过注解反射来是现实数据库框架，实现基本的数据库增删改查，支持主键自增长，使用方便，功能简洁。

使用jitpack仓库发布：
gradle
```
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
	 implementation 'com.github.fishteam1:MDao:1.0.0'
}
```

创建数据库和字段:
```
@DbTabel("news")
public class News {
    @DbField("title")
    private String title;
    @DbField("content")
    private String content;
    @DbField("author")
    private String author;
    @DbField("date")
    private String date;
}
```

插入数据：
```
插入name为“morse”，password为的“123456”的对象：
User user = new User();
user.setName("morse");
user.setPassword("123456");
long id = DBManager.getManagerDao(User.class).insert(user);
```
查询数据：
```
查找name为“morse”的user对象：
User user = new User();
user.setName("morse");
List<User> users = DBManager.getManagerDao(User.class).query(user);
```
更新数据：
```
找到name为“morese”，password为“12345”的user，更新password为“123456”：
User user = new User();
user.setName("morse");
user.setPassword("12345");
User user1 = new User();
user1.setPassword("123456");
DBManager.getManagerDao(User.class).update(user, user1);
```
删除数据：
```
删除id为1的对象：
User user = new User();
user.setId(1);
DBManager.getManagerDao(User.class).delete(user);
```
