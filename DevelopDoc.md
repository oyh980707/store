# 学子商城项目开发过程

## 处理登录注册功能

### 1. 分析项目

当需要开发某个项目时，首先，应该分析这个项目中，需要处理哪些种类的数据！例如：用户、商品、商品类别、收藏、订单、购物车、收货地址……

然后，将以上这些种类的数据的处理排个顺序，即先处理哪种数据，后处理哪种数据！通常，应该先处理基础数据，再处理所相关的数据，例如需要先处理商品数据，才可以处理订单数据，如果多种数据之间没有明显的关联，则应该先处理简单的，再处理较难的！则以上这些数据的处理顺序应该是：用户 > 收货地址 > 商品类别 > 商品 > 收藏 > 购物车 > 订单。

当确定了数据处理顺序后，就应该分析某个用户对应的功能有哪些，以“用户”数据为例，相关功能有：注册、登录、修改密码、修改资料、上传头像……

然后，还是需要确定以上功能的开发顺序，通常，遵循“增 > 查 > 删 > 改”的顺序，则以上功能的开发顺序应该是：注册 > 登录 > 修改密码 > 修改资料 > 上传头像。

每个功能的开发都应该遵循“创建数据表 > 创建实体类 > 持久层 > 业务层 > 控制器层 > 前端页面”。

**一次只解决一个问题！**

### 2. 用户-注册-创建数据表

首先，需要创建数据库：

	CREATE DATABASE oyh_store;

然后，使用该数据库：

	USE oyh_store;

再创建数据表：

	CREATE TABLE o_user (
		uid INT AUTO_INCREMENT COMMENT '用户id',
		username VARCHAR(20) UNIQUE NOT NULL COMMENT '用户名',
		password CHAR(32) NOT NULL COMMENT '密码',
		salt CHAR(36) COMMENT '盐值',
		gender INT COMMENT '性别：0-女，1-男',
		phone VARCHAR(20) COMMENT '电话号码',
		email VARCHAR(50) COMMENT '邮箱',
		avatar VARCHAR(50) COMMENT '头像',
		is_delete INT COMMENT '是否标记为删除：0-未删除，1-已删除',
		created_user VARCHAR(20) COMMENT '创建人',
		created_time DATETIME COMMENT '创建时间',
		modified_user VARCHAR(20) COMMENT '最后修改人',
		modified_time DATETIME COMMENT '最后修改时间',
		PRIMARY KEY (uid)
	) DEFAULT CHARSET=UTF8;

### 3. 用户-注册-创建实体类

首先，从FTP服务器下载项目的压缩包，通过**Import** > **Existing Maven Projects**导入项目。

由于项目中添加了数据库相关依赖，在启动时，SpringBoot就会读取相关配置，如果没有，则报错，所以，就需要将此前项目中的配置复制到此项目中，注意修改数据库的名称为`oyh_store`：

	spring.datasource.url=jdbc:mysql://localhost:3306/oyh_store?useUnicode=true&characeterEncoding=utf-8&serverTimezone=Asia/Shanghai
	spring.datasource.username=root
	spring.datasource.password=980707
	
	mybatis.mapper-locations=classpath:mappers/*.xml

然后，在单元测试类中，先测试运行默认存在的空方法，测试通过后，自行编写并测试数据库连接是否正确：

	@Autowired
	DataSource dataSource;
	
	@Test
	public void getConnection() throws SQLException {
		Connection conn = dataSource.getConnection();
		System.err.println(conn);
	}

接下来，应该创建实体类，在项目中，后续还会出现更多的数据表，而每张表中都会有`created_user`等日志字段，对应的每个实体类中也需要有这些属性，为了避免重复声明这些属性，可以先创建`com.loveoyh.store.entity.BaseEntity`实体类的基类：

	abstract class BaseEntity implements Serializable {
		private String createdUser;
		private Date createdTime;
		private String modifiedUser;
		private Date modifiedTime;
	
		// SET/GET
	}

然后再`com.loveoyh.store.entity.User`实体类，继承自以上基类：

	public class User extends BaseEntity {
	
		private Integer uid;
		private String username;
		private String password;
		private String salt;
		private Integer gender;
		private String phone;
		private String email;
		private String avatar;
		private Integer isDelete;
	
		// SET/GET/toString/equals和hashCode
	}

### 4. 用户-注册-持久层

**4.1. 规划SQL语句**

注册的本质是向数据表中插入新的数据，则执行的SQL语句应该是：

	insert into o_user (除了uid以外的字段列表) values (匹配的User类中属性名的占位符)

通常，用户注册时，不允许使用已经存在的用户名，即用户名都是唯一的，这一点应该在Java程序中进行检查，而不应该直接将数据送到数据库服务器尝试执行插入！所以，可以根据用户尝试注册的用户名查询用户数据，如果查询结果为null，则表示用户名没有被占用，可以正常注册，如果查询到了结果，则表示用户名已经被占用，则不会执行插入数据！此次查询时需要执行的SQL语句大致是：

	select uid from o_user where username=?

**4.2. 接口与抽象方法**

首先，应该在启动类之前添加`@MapperScan("com.loveoyh.store.mapper")`以配置持久层接口所在的包。

创建`com.loveoyh.store.mapper.UserMapper`持久层接口，并在接口中添加抽象方法：

	Integer insert(User user);
	
	User findByUsername(String username);

**4.3. 配置映射**

检查在**application.properties**中是否配置了映射文件的位置：

	mybatis.mapper-locations=classpath:mappers/*.xml

然后，在**src/main/resources**下创建名为**mappers**的文件夹，在该文件夹中添加**UserMapper.xml**文件，并配置该文件：

	<mapper namespace="com.loveoyh.store.mapper.UserMapper">
	
		<!-- 插入用户数据 -->
		<!-- Integer insert(User user) -->
		<insert id="insert"
			useGeneratedKeys="true"
			keyProperty="uid">
			INSERT INTO o_user (
				username, password,
				salt, gender,
				phone, email,
				avatar, is_delete,
				created_user, created_time,
				modified_user, modified_time
			) VALUES (
				#{username}, #{password},
				#{salt}, #{gender},
				#{phone}, #{email},
				#{avatar}, #{isDelete},
				#{createdUser}, #{createdTime},
				#{modifiedUser}, #{modifiedTime}
			)
		</insert>
		
		<!-- 根据用户名查询用户数据 -->
		<!-- User findByUsername(String username) -->
		<select id="findByUsername"
			resultType="com.loveoyh.store.entity.User">
			SELECT 
				uid
			FROM 
				o_user 
			WHERE 
				username=#{username}
		</select>
		
	</mapper>


完成后，在**src/test/java**下创建`com.loveoyh.store.mapper.UserMapperTests`用户数据持久层的测试类，并在类中编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class UserMapperTests {
	
		@Autowired
		private UserMapper mapper;	
	
		@Test
		public void insert() {
			User user = new User();
			user.setUsername("root");
			user.setPassword("1234");
			Integer rows = mapper.insert(user);
			System.err.println("rows=" + rows);
		}
	
		@Test
		public void findByUsername() {
			String username = "root";
			User user = mapper.findByUsername(username);
			System.err.println(user);
		}
		
	}


### 5. 用户-注册-业务层

**5.1. 业务层的基本定位**

在MVC设计理念中，M表示的是Model，即数据模型，它由持久层和业务层共同构成！持久层负责完成数据操作，即增删改查，业务层负责组织业务流程和管理业务逻辑，业务更多在表现为用户能操作的某1个“功能”，但是，对于程序员来说，可能是更多的细小的功能来组成的！之所以需要业务层，是因为需要通过业务层来保证数据的安全(数据必须经过业务层的各种流程和逻辑才产生或发生变化)！

**5.2. 规划异常**

考虑当前的“注册”功能可能会抛出哪些异常！

在“注册”时，需要先检查用户名是否被占用，如果已经被占用，则不允许注册 ，就是一种“操作失败”，则应该有“用户名占用异常”被抛出！

如果用户名没有被占用，则允许注册，将执行数据表的`INSERT`操作，凡是数据表的增、删、改操作，都是有可能出现操作失败的！则应该抛出“插入数据异常”。

在业务层，一旦视为“操作失败”，对异常的处理都应该是“抛出”，而不是自行处理！因为业务层不适合进行处理！

所以，此次需要创建2个异常类`com.loveoyh.store.service.ex.UsernameDuplicateException`和`com.loveoyh.store.service.ex.InsertException`，另外还需要创建这2个异常类公共的父级异常类`com.loveoyh.store.service.ex.ServiceException`，它们都应该是`RuntimeException`的子孙类：

	RuntimeException
		ServiceException
			UsernameDuplicateException
			InsertException

**5.3. 接口与抽象方法**

在设计业务层时，应该先有业务层的接口，并在接口中定义抽象方法，后续，外界(当前Model以外，例如Controller或其它Service等)调用时，是基于接口来声明对象，并调用方法的，所以，此处使用接口是一种解耦的做法！

先创建`com.loveoyh.store.service.IUserService`业务层接口，并添加抽象方法：

	void reg(User user) throws UsernameDuplicateException, InsertException;

关于业务层的抽象方法的设计原则：

1. 返回值：仅以操作成功为前提来设计返回值；

2. 方法名：尽量体现业务，例如使用`reg`或`regist`或`register`表示注册，使用`login`表示登录；

3. 参数列表：一定是客户端可以提供的数据，或来自于Session中的数据，且足够调用持久层的各方法；

4. 异常：把用户操作失败的可能，都设计出各种异常，并把这些异常都添加到方法的声明中。

**5.4. 实现类与重写方法**

创建`com.loveoyh.store.service.impl.UserServiceImpl`业务层实现类，实现以上`IUserService`接口，添加`@Service`注解，并在类中声明`@Autowired private UserMapper userMapper;`持久层对象：

	@Service
	public class UserServiceImpl implements IUserService {
	
		@Autowired
		private UserMapper userMapper;
	
	}

然后，重写接口中定义的抽象方法：

	public void reg(User user) throws UsernameDuplicateException, InsertException {
		// 根据参数user对象中的username属性查询数据：userMapper.findByUsername()
		// 判断查询结果是否不为null（查询结果是存在的）
		// 是：用户名已被占用，抛出UsernameDuplicateException
	
		// TODO 得到盐值
		// TODO 基于参数user对象中的password进行加密，得到加密后的密码
		// TODO 将加密后的密码和盐值封装到user中
	
		// 将user中的isDelete设置为0
	
		// 封装user中的4个日志属性
	
		// 执行注册：userMapper.insert(user)
	}

具体实现为：

	@Override
	public void reg(User user) throws UsernameDuplicateException, InsertException {
		// 根据参数user对象中的username属性查询数据：userMapper.findByUsername()
		String username = user.getUsername();
		User result = userMapper.findByUsername(username);
		// 判断查询结果是否不为null（查询结果是存在的）
		if (result != null) {
			// 是：用户名已被占用，抛出UsernameDuplicateException
			throw new UsernameDuplicateException(
				"注册失败！尝试注册的用户名(" + username + ")已经被占用！");
		}
	
		// 得到盐值
		System.err.println("reg() > password=" + user.getPassword());
		String salt = UUID.randomUUID().toString().toUpperCase();
		// 基于参数user对象中的password进行加密，得到加密后的密码
		String md5Password = getMd5Password(user.getPassword(), salt);
		// 将加密后的密码和盐值封装到user中
		user.setSalt(salt);
		user.setPassword(md5Password);
		System.err.println("reg() > salt=" + salt);
		System.err.println("reg() > md5Password=" + md5Password);
		
		// 将user中的isDelete设置为0
		user.setIsDelete(0);
	
		// 封装user中的4个日志属性
		Date now = new Date();
		user.setCreatedUser(username);
		user.setCreatedTime(now);
		user.setModifiedUser(username);
		user.setModifiedTime(now);
	
		// 执行注册：userMapper.insert(user)
		Integer rows = userMapper.insert(user);
		if (rows != 1) {
			throw new InsertException(
				"注册失败！写入数据时出现未知错误！请联系系统管理员！");
		}
	}
	
	/**
	 * 对密码进行加密
	 * @param password 原始密码
	 * @param salt 盐值
	 * @return 加密后的密码
	 */
	private String getMd5Password(String password, String salt) {
		// 规则：对password+salt进行3重加密
		String str = password + salt;
		for (int i = 0; i < 3; i++) {
			str = DigestUtils
				.md5DigestAsHex(str.getBytes()).toUpperCase();
		}
		return str;
	}

**注意：特别是在执行插入数据时，业务层除了需要保证业务流程和业务逻辑，还需要保证数据的完整性！**

完成后，在**src/test/java**下创建`com.loveoyh.store.service.UserServiceTests`测试类，编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class UserServiceTests {
	
		@Autowired
		IUserService service;
		
		@Test
		public void reg() {
			try {
				User user = new User();
				user.setUsername("Digest");
				user.setPassword("1234");
				service.reg(user);
				System.err.println("OK.");
			} catch (ServiceException e) {
				System.err.println(e.getClass().getName());
				System.err.println(e.getMessage());
			}
		}
		
	}

### 6. 用户-注册-控制器层

**6.1. 处理异常**

在编写处理请求的控制器及方法之前，还应该对异常进行统一处理！

首先，需要创建响应结果的数据类型`com.loveoyh.store.entity.JsonResult`：

	/**
	 * 向客户端响应操作结果的数据类型
	 * 
	 * @param <T> 向客户端响应的数据的类型
	 */
	public class JsonResult {
		/** 表示成功状态标志 */
		public static final int SUCCESS = 0;
		/** 表示出错状态标志 */
		public static final int ERROR = 1;
		
		/** 状态 */
		private int state;
		/** 错误消息 */
		private String message;
		/** 返回正确的时候的数据 */
		private T data;
	
		public JsonResult() {
		}
		
		public JsonResult(String message) {
			state = ERROR;
			this.message = message;
		}
		
		public JsonResult(T data) {
			state = SUCCESS;
			this.data = data;
		}
		
		...
	}

在统一处理异常时，相关代码只能作用于当前控制器类，为了使得整个项目都可以使用这个统一处理异常的机制，应该把相应代码添加在控制器类的基类`com.loveoyh.store.controller.BaseController`中，所以：

	public class BaseController {
	
		@ExceptionHandler(ServiceException.class)
		public JsonResult handleException(Throwable e) {
			JsonResult jr = new JsonResult();
			jr.setMessage(e.getMessage());
	
			if (e instanceof UsernameDuplicateException) {
				jr.setState(2);
			} else if (e instanceof InsertException) {
				jr.setState(3);
			}
	
			return js;
		}
	
	}

**6.2. 设计请求**

需要事先规划用户的请求是什么样的，例如用户向哪个URL发出请求表示“注册”，请求方式是哪种，是否需要提交某些请求参数等，以及最终服务器端向客户端响应什么样的数据：

	请求路径：/users/reg
	请求参数：User user
	请求类型：POST
	响应数据：JsonResult

**6.3. 处理请求**

创建`com.loveoyh.store.controller.UserController`控制器类，继承自`BaseController`，添加`@RestController`和`@RequestMapping("users")`注解，并在类中添加`@Autowired private IUserService userService;`业务层对象，并在类中添加处理请求的方法：

	@ReuqestMapping("reg")
	public JsonResult reg(User user) {
		// 执行注册
		userService.reg(user);
		// 返回成功
		JsonResult jr = new JsonResult();
		jr.setState(1);
		return jr;
	}

打开浏览器，输入`http://localhost:8080/users/reg?username=Alex&password=1234`进行测试。

### 7. 用户-注册-前端页面

主要对于ajax异步发起请求，参考代码如下：

```javas
		$(document).ready(function(){
			$("#btn-reg").click(function(){
				$.ajax({
					"url":"/users/reg",
					"data":$("#form-reg").serialize(),
					"type":"post",
					"dataType":"json",
					"success":function(data){
						if(data.state == 0){
							alert("注册成功！");
							//跳转页面
						} else {
							alert(data.message);
						}
					}
				});
			});
		});
```

### 8. 用户-登录-持久层

**a. 规划SQL语句**

登录验证的做法应该是：根据用户名查询数据是否存在，如果存在，则取出必要的数据，例如密码，然后，在Java程序中验证密码即可。

如果用户名匹配的数据是存在的，需要取出的数据有：密码，盐，是否标记为删除，uid，用户名。对应的SQL语句大致是：

	select uid,username,password,salt,is_delete from o_user where username=?

**b. 接口与抽象方法**

在接口中已经存在`findByUsername()`方法，则无须重复添加。

**c. 配置映射**

只需在原有的`findByUsername()`方法映射的SQL语句中，添加查询更多的字段即可！

### 9. 用户-登录-业务层

**a. 规划异常**

规划异常，应该是列举此次操作中可能存在的操作失败，包括用户提交不合理甚至错误的数据，或不符合逻辑的数据，都是失败的！

在“登录”时，用户提交的用户名可能是未被注册的，即不存在的，对于这种情况，应该抛出对应的异常：`UserNotFoundException`；

也可能查询到了用户名匹配的数据，但是，是被标记为删除的，这种用户数据也是不允许登录的，也应该抛出异常：`UserNotFoundException`；

在验证密码时，还可能出现密码不匹配的问题，也是不允许登录的，则抛出异常：`PasswordNotMatchException`。

则需要创建`com.loveoyh.store.service.ex.UserNotFoundException`和`com.loveoyh.store.service.ex.PasswordNotMatchException`，它们都是`ServiceException`的子类。

**b. 接口与抽象方法**

在`IUserService`接口中添加新的抽象方法：

	User login(String username, String password) throws UserNotFoundException, PasswordNotMatchException;

**c. 实现类与重写方法**

在`UserServiceImpl`类中重写接口中的抽象方法：

	public User login(String username, String password) throws UserNotFoundException, PasswordNotMatchException {
		// 根据参数username执行查询用户数据
		// 判断查询结果是否为null
		// 抛出：UserNotFoundException
	
		// 判断查询结果中的isDelete为1
		// 抛出：UserNotFoundException
	
		// 从查询结果中获取盐值
		// 根据参数password和盐值一起进行加密，得到加密后的密码
		// 判断查询结果中的password和以上加密后的密码是否不一致
		// 抛出：PasswordNotMatchException
	
		// 将查询结果中的password、salt、isDelete设置为null
		// 返回查询结果
	}

具体实现为：

	@Override
	public User login(String username, String password) throws UserNotFoundException, PasswordNotMatchException {
		// 根据参数username执行查询用户数据
		User result = userMapper.findByUsername(username);
		// 判断查询结果是否为null
		if (result == null) {
			// 抛出：UserNotFoundException
			throw new UserNotFoundException(
				"登录失败！用户名不存在！");
		}
	
		// 判断查询结果中的isDelete为1
		if (result.getIsDelete() == 1) {
			// 抛出：UserNotFoundException
			throw new UserNotFoundException(
				"登录失败！用户名不存在！");
		}
	
		// 从查询结果中获取盐值
		String salt = result.getSalt();
		// 根据参数password和盐值一起进行加密，得到加密后的密码
		String md5Password = getMd5Password(password, salt);
		// 判断查询结果中的password和以上加密后的密码是否不一致
		if (!result.getPassword().equals(md5Password)) {
			// 抛出：PasswordNotMatchException
			throw new PasswordNotMatchException(
				"登录失败！密码错误！");
		}
	
		// 将查询结果中的password、salt、isDelete设置为null
		result.setPassword(null);
		result.setSalt(null);
		result.setIsDelete(null);
		// 返回查询结果
		return result;
	}

在`UserServiceTests`中编写新的测试方法，以执行单元测试：

	@Test
	public void login() {
		try {
			String username = "root";
			String password = "1234";
			User user = service.login(username, password);
			System.err.println(user);
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

### 10. 用户-登录-控制器层

**a. 处理异常**

此次业务层抛出了新的异常，则需在`BaseController`的处理异常的方法中，添加更多的分支，对这些新的异常进行处理。

**b. 设计请求**

	请求路径：/users/login
	请求参数：String username, String password, HttpSession session
	请求方式：POST
	响应数据：JsonResult

通常，请求路径中，后半部分表示当前功能的名称，可以与业务层方法的名称保持一致！

**c. 处理请求**

在`UserController`添加处理请求的方法：

	@RequestMapping("login")
	public JsonResult login(
		String username, String password,
		HttpSession session) {
		// 执行登录，获取登录返回结果
		User user = userService.login(username, password);
		// 向Session中封装数据
		session.setAttribute("uid", user.getUid());
		session.setAttribute("username", user.getUsername());
		// 向客户端响应操作成功
		return new JsonResult<>(SUCCESS, user);
	}

完成后，通过`http://localhost:8080/users/login?username=root&password=1234`执行单元测试。

另：在`JsonResult`和`User`类之前添加`@JsonInclude(Include.NON_NULL)`，可以在输出JSON数据时，不添加为null的属性。该注解也可以添加在类的某个属性之前，表示该属性值为null时是否需要输出到JSON字符串中，取值学可以是`@JsonInclude(Include.ALWAYS)`，如果整个项目统一约定“所有为null的属性都不输出到JSON字符串中”，则可以在`application.propertiest`中添加`spring.jackson.default-property-inclusion=non-null`进行全局的配置。

### 11. 用户-登录-前端页面

主要对于ajax异步发起请求，参考代码如下：

```javas
$(document).ready(function(){
	$("#btn-login").click(function(){
		$.ajax({
			"url":"/users/login",
			"data":$("#form-login").serialize(),
			"type":"post",
			"dataType":"json",
			"success":function(data){
				if(data.state == 0){
					alert("登录成功！");
					//跳转页面
				} else {
					alert(data.message);
				}
			}
		});
	});
});
```

### 12. 用户-修改密码-持久层

**a. 规划SQL语句**

修改密码的SQL语句大致是：

	update o_user set password=?, modified_user=?, modified_time=? where uid=?

在执行更新之前，还应该检查用户数据是否正常，并验证其原密码是否正确，则需要执行：

	select is_delete, password, salt from o_user where uid=?

**b. 接口与抽象方法**

需要在`UserMapper`接口中添加新的抽象方法：

	Integer updatePassword(
		@Param("uid") Integer uid, 
		@Param("password") String password,
		@Param("modifiedUser") String modifiedUser,
		@Param("modifiedTime") Date modifiedTime);
	
	User findByUid(Integer uid);

注意：在接口中，应该将各方法按照某种顺序进行排列！

**c. 配置映射**

在`UserMapper.xml`中配置以上2个方法的映射：

然后，在`UserMapperTests`中编写并执行单元测试：

**注意：用于测试的数据，在测试完成之后，需要将这些数据删除！**

### 13. 用户-修改密码-业务层

**a. 规划异常**

此次更新密码之前需要检查用户数据是否正常，例如用户数据是否存在，是否被标记为删除，则可能抛出`UserNotFoundException`；

在执行更新之前还需要验证原密码是否正确，则可能抛出`PasswordNotMatchException`；

在执行更新过程中，也可能出现更新失败，返回的受影响的行数不是预期值，则可能抛出`UpdateException`。

所以，需要创建`com.loveoyh.store.service.ex.UpdateException`。

**b. 接口与抽象方法**

在`IUserService`中添加抽象方法：

	void changePassword(Integer uid, String username, String oldPassword, String newPassword) throws UserNotFoundException, PasswordNotMatchException, UpdateException;

**c. 实现类与重写方法**

在`UserServiceImpl`中实现以上抽象方法：

	public void changePassword(Integer uid, String username, String oldPassword, String newPassword) throws UserNotFoundException, PasswordNotMatchException, UpdateException {
		// 根据参数uid查询用户数据
		// 判断查询结果是否为null
		// 抛出：UserNotFoundException
	
		// 判断查询结果中的isDelete为1
		// 抛出：UserNotFoundException
	
		// 从查询结果中获取盐值
		// 根据参数oldPassword和盐值一起进行加密，得到加密后的密码
		// 判断查询结果中的password和以上加密后的密码是否不一致
		// 抛出：PasswordNotMatchException
	
		// 根据参数newPassword和盐值一起进行加密，得到加密后的密码
		// 创建当前时间对象
		// 执行更新密码，并获取返回的受影响的行数
		// 判断受影响的行数是否不为1
		// 抛出：UpdateException
	}

具体实现为：

	@Override
	public void changePassword(Integer uid, String username, String oldPassword, String newPassword)
			throws UserNotFoundException, PasswordNotMatchException, UpdateException {
		System.err.println("changePassword() ---> BEGIN:");
		System.err.println("changePassword() 原密码=" + oldPassword);
		System.err.println("changePassword() 新密码=" + newPassword);
		
		// 根据参数uid查询用户数据
		User result = userMapper.findByUid(uid);
		// 判断查询结果是否为null
		if (result == null) {
			// 抛出：UserNotFoundException
			throw new UserNotFoundException(
				"修改密码失败！用户名不存在！");
		}
	
		// 判断查询结果中的isDelete为1
		if (result.getIsDelete() == 1) {
			// 抛出：UserNotFoundException
			throw new UserNotFoundException(
				"修改密码失败！用户名不存在！");
		}
	
		// 从查询结果中获取盐值
		String salt = result.getSalt();
		// 根据参数oldPassword和盐值一起进行加密，得到加密后的密码
		String oldMd5Password = getMd5Password(oldPassword, salt);
		System.err.println("changePassword() 盐值=" + salt);
		System.err.println("changePassword() 原密码加密=" + oldMd5Password);
		System.err.println("changePassword() 正确密码=" + result.getPassword());
		// 判断查询结果中的password和以上加密后的密码是否不一致
		if (!result.getPassword().equals(oldMd5Password)) {
			// 抛出：PasswordNotMatchException
			throw new PasswordNotMatchException(
				"修改密码失败！原密码错误！");
		}
	
		// 根据参数newPassword和盐值一起进行加密，得到加密后的密码
		String newMd5Password = getMd5Password(newPassword, salt);
		System.err.println("changePassword() 新密码加密=" + newMd5Password);
		// 创建当前时间对象
		Date now = new Date();
		// 执行更新密码，并获取返回的受影响的行数
		Integer rows = userMapper.updatePassword(uid, newMd5Password, username, now);
		// 判断受影响的行数是否不为1
		if (rows != 1) {
			// 抛出：UpdateException
			throw new UpdateException(
				"修改密码失败！更新密码时出现未知错误！");
		}
		
		System.err.println("changePassword() ---> END.");
	}

在`UserServiceTests`中编写并执行单元测试：

	/**
	* 测试更新密码
	*/
	@Test
	public void testChangePassword() {
	    try {
	        Integer uid = 1;
	        String username = "root";
	        String oldPassword = "123456";
	        String newPassword = "980707";
	        userService.changePassword(uid, username, oldPassword, newPassword);
	        System.err.println("OK.");
	    } catch (ServiceException e) {
	        System.err.println(e.getClass().getName());
	        System.err.println(e.getMessage());
	    }
	}

### 14. 用户-修改密码-控制器层

**a. 处理异常**

此次业务层抛出了新的异常：`UpdateException`，则需要在`BaseController`中进行处理！

**b. 设计请求**

	请求路径：/users/change_password
	请求参数：String oldPassword, String newPassword, HttpSession session
	请求方式：POST
	响应数据：JsonResult

**c. 处理请求**

	@RequestMapping("change_password")
	public JsonResult changePassword(
		@RequestParam("old_password") String oldPassword, 
		@RequestParam("new_password") String newPassword, 
		HttpSession session) {
		// 从session中获取uid和username
		Integer uid = Integer.valueOf(session.getAttribute("uid").toString());
		String username = session.getAttribute("username").toString();
		// 执行修改密码
		userService.changePassword(uid, username, oldPassword, newPassword);
		// 响应修改成功
		return new JsonResult<>(SUCCESS);
	}

完成后，启动项目，在浏览器中，先登录，然后通过`http://localhost:8080/users/change_password?old_password=1234&new_password=8888`进行测试。

后续，会有越来越多的操作需要先登录才可以进行，为了保证统一处理，则应该在项目中添加`com.loveoyh.store.interceptor.LoginInterceptor`登录拦截器：

	/**
	 * 登录拦截器
	 */
	public class LoginInterceptor implements HandlerInterceptor {
	
		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {
			// 验证用户是否登录，如果已登录，放行，如果未登录，拦截，并重定向到登录页面
			// 获取Session对象
			HttpSession session = request.getSession();
			// 判断Session中有没有uid，因为登录成功时往Session中存入了uid，所以可以使用uid作为判断标准
			if (session.getAttribute("uid") == null) {
				// 重定向到登录页面
				response.sendRedirect("/web/login.html");
				// 执行拦截
				return false;
			}
			// 执行放行
			return true;
		}
	
	}

然后，还需要配置拦截器，在SpringBoot项目中，需要使用自定义类进行配置，且配置时，需要该类实现`WebMvcConfigurer`接口，并添加`@Configuration`注解：

	/**
	 * 登录拦截器的配置类
	 */
	@Configuration
	public class LoginInterceptorConfigurer implements WebMvcConfigurer {
	
		@Override
		public void addInterceptors(
				InterceptorRegistry registry) {
			// 创建拦截器对象
			HandlerInterceptor interceptor
				= new LoginInterceptor();
			
			// 创建白名单
			List<String> excludePaths = new ArrayList<>();
			excludePaths.add("/js/**");
			excludePaths.add("/css/**");
			excludePaths.add("/bootstrap3/**");
			excludePaths.add("/images/**");
			excludePaths.add("/web/register.html");
			excludePaths.add("/web/login.html");
			excludePaths.add("/users/reg");
			excludePaths.add("/users/login");
			
			// 注册拦截器，并设置黑白名单
			registry.addInterceptor(interceptor)
				.addPathPatterns("/**")
				.excludePathPatterns(excludePaths);
		}
	
	}

### 15. 用户-修改密码-前端页面

主要对于ajax异步发起请求，参考代码如下：

```javas
$(document).ready(function(){
	$("#btn-change-password").click(function(){
		$.ajax({
			"url":"/users/change_password",
			"data":$("#form-change-password").serialize(),
			"type":"post",
			"dataType":"json",
			"success":function(data){
				if(data.state == 0){
					alert("修改成功！");
					//跳转页面
				} else {
					alert(data.message);
				}
			},
			"error":function(){
				/*
					所有非2开头的状态码会走到这里，所以程序可以尽量避免4,5开头的
					状态码，3开头的重定向将会在这里跳转
				*/
				alert("您的登录信息已经过期！请重新登录！");
				//location.href = "/web/login.html";
			}
		});
	});
});
```

### 16. 用户-修改个人资料-持久层

**a. 规划SQL语句**

执行修改个人资料的SQL语句大致是：

	update o_user set phone=?, email=?, gender=?, modified_user=?, modified_time=? where uid=?

在执行修改之前，还应该检查用户的数据是否正确，例如是否存在和是否标记为删除，这项检查所需的功能已经完成，无需重复开发。

另外，“修改个人资料”和“修改密码”不同，首先需要完成界面的显示！即用户打开界面时，应该显示当前登录的用户的相关信息！则需要：

	select username,phone,email,gender from o_user where uid=?


根据Uid查询数据的持久层功能已经存在，后续在开发时，只需要补充查询的字段列表即可。

**b. 接口与抽象方法**

在`UserMapper.java`接口添加新的抽象方法：

	Integer updateInfo(User user);

**c. 配置映射**

在`UserMapper.xml`中配置以上抽象方法的映射：

	<!-- 更新用户基本资料 -->
	<!-- Integer updateInfo(User user) -->
	<update id="updateInfo">
		UPDATE 
			o_user 
		SET 
			<if test="phone != null">
			phone=#{phone}, 
			</if>
			<if test="email != null">
			email=#{email}, 
			</if>
			<if test="gender != null">
			gender=#{gender}, 
			</if>
			modified_user=#{modifiedUser}, 
			modified_time=#{modifiedTime} 
		WHERE 
			uid=#{uid}
	</update>

修改原有的`findByUid()`的配置，查询更多的字段：

	<!-- 根据用户名查询用户数据 -->
	<!-- User findByUid(Integer uid) -->
	<select id="findByUid"
		resultType="com.loveoyh.store.entity.User">
		SELECT 
			username, phone,
			email, gender,
			password, salt,
			is_delete AS isDelete
		FROM 
			o_user 
		WHERE 
			uid=#{uid}
	</select>

完成后，编写并执行单元测试：

	@Test
	public void updateInfo() {
		User user = new User();
		user.setUid(7);
		user.setPhone("13800138007");
		user.setEmail("root@qq.com");
		user.setGender(1);
		Integer rows = mapper.updateInfo(user);
		System.err.println("rows=" + rows);
	}

### 17. 用户-修改个人资料-业务层

**a. 规划异常**

此次客户端涉及的操作有2个：查询当前登录的用户信息，执行修改用户的基本资料。

首先，这个查询功能是不存在错误的。

关于修改功能，可能涉及的异常：`UserNotFoundException`、`UpdateException`。

**b. 接口与抽象方法**

在`IUserService`中添加2个抽象方法：

	User getByUid(Integer uid);
	
	void changeInfo(User user) throws UserNotFoundException, UpdateException;

**c. 实现类与重写方法**

在`UserServiceImpl`中重写以上2个抽象方法：

	public User getByUid(Integer uid) {
		// 根据uid查询用户数据
	
		// 如果查询到数据，则需要将查询结果中的password、salt、is_delete设置为null
	
		// 将查询结果返回
	}
	
	public void changeInfo(User user) throws UserNotFoundException, UpdateException {
		// 根据参数user中的uid，即user.getUid()查询数据
		// 检查查询结果是否存在，是否标记为删除
	
		// 创建当前时间对象
		// 将时间封装到参数user中
		// 执行修改个人资料：mapper.updateInfo(user) > update o_user set phone=?, email=?, gender=?, modified_user=?, modified_time=? where uid=?
		// 判断以上修改时的返回值是否不为1
		// 抛出：UpdateException 
	}

具体实现为：

	@Override
	public User getByUid(Integer uid) {
		// 根据uid查询用户数据
		User result = userMapper.findByUid(uid);
	
		// 如果查询到数据，则需要将查询结果中的password、salt、is_delete设置为null
		if (result != null) {
			result.setPassword(null);
			result.setSalt(null);
			result.setIsDelete(null);
		}
	
		// 将查询结果返回
		return result;
	}
	
	@Override
	public void changeInfo(User user) throws UserNotFoundException, UpdateException {
		// 根据参数user中的uid，即user.getUid()查询数据
		User result = userMapper.findByUid(user.getUid());
		// 检查查询结果是否存在，是否标记为删除
		// 判断查询结果是否为null
		if (result == null) {
			// 抛出：UserNotFoundException
			throw new UserNotFoundException(
				"修改个人资料失败！用户数据不存在！");
		}
	
		// 判断查询结果中的isDelete为1
		if (result.getIsDelete() == 1) {
			// 抛出：UserNotFoundException
			throw new UserNotFoundException(
				"修改个人资料失败！用户数据不存在！");
		}
	
		// 创建当前时间对象
		Date now = new Date();
		// 将时间封装到参数user中
		user.setModifiedUser(user.getUsername());
		user.setModifiedTime(now);
		// 执行修改个人资料：mapper.updateInfo(user) > update o_user set phone=?, email=?, gender=?, modified_user=?, modified_time=? where uid=?
		Integer rows = userMapper.updateInfo(user);
		// 判断以上修改时的返回值是否不为1
		if (rows != 1) {
			// 抛出：UpdateException 
			throw new UpdateException(
				"修改个人资料失败！更新用户数据时出现未知错误！");
		}
	}

在`UserServiceTests`中编写并执行单元测试：

	@Test
	public void getByUid() {
		Integer uid = 7;
		User user = service.getByUid(uid);
		System.err.println(user);
	}
	
	@Test
	public void changeInfo() {
		try {
			User user = new User();
			user.setUid(7);
			user.setUsername("超级管理员X");
			user.setPhone("13700137007");
			service.changeInfo(user);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

### 18. 用户-修改个人资料-控制器层

**a. 处理异常**

无

**b. 设计请求**

关于获取当前登录的用户信息的请求：

	请求路径：/users/get_info
	请求参数：HttpSession session
	请求方式：GET
	响应数据：JsonResult

关于执行修改个人信息的请求：

	请求路径：/users/change_info
	请求参数：User user, HttpSession session
	请求方式：POST
	响应数据：JsonResult

**c. 处理请求**

先在`BaseController`中声明2个方法，分别用于从Session中获取uid和用户名：

	/**
	 * 从Session中获取当前登录的用户的id
	 * @param session
	 * @return 当前登录的用户的id
	 */
	protected final Integer getUidFromSession(HttpSession session) {
		return Integer.valueOf(session.getAttribute("uid").toString());
	}
	
	/**
	 * 从Session中获取当前登录的用户名
	 * @param session
	 * @return 当前登录的用户名
	 */
	protected final String getUsernameFromSession(HttpSession session) {
		return session.getAttribute("username").toString();
	}

然后，在`UserController`中处理请求：

	@GetMapping("get_info")
	public JsonResult getByUid(
			HttpSession session) {
		// 从session中获取uid
		Integer uid = getUidFromSession(session);
		// 查询匹配的数据
		User data = userService.getByUid(uid);
		// 响应
		return new JsonResult<>(SUCCESS, data);
	}
	
	@RequestMapping("change_info")
	public JsonResult changeInfo(
		User user, HttpSession session) {
		// 从session中获取uid和username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		// 将uid和username封装到user中
		user.setUid(uid);
		user.setUsername(username);
		// 执行修改
		userService.changeInfo(user);
		// 响应
		return new JsonResult<>(SUCCESS);
	}

打开浏览器，先登录，分别通过`http://localhost:8080/users/get_info`和`http://localhost:8080/users/change_info?email=root@tedu.cn&phone=13100131007&gender=1`进行测试。

### 19. 用户-修改个人资料-前端页面



### 20. 用户-上传头像-持久层

**a. 规划SQL语句**

用户上传头像时，服务器端需要处理的任务有2个，第1是将文件保存到指定的位置，第2是将文件保存的路径存储到数据表中。

在数据表相关的操作中，本质上就是修改表中的字段即可：

	update o_user set avatar=?, modified_user=?, modified_time=? where uid=?

在执行修改之前，还应该检查用户数据是否正常，此前已经完成，无需再次开发。

**b. 接口与抽象方法**

在`UserMapper`接口中添加抽象方法：

	/**
	 * 更新用户的头像
	 * @param uid 用户的id
	 * @param avatar 头像的路径
	 * @param modifiedUser 修改执行人
	 * @param modifiedTime 修改时间
	 * @return 受影响的行数
	 */
	Integer updateAvatar(
			@Param("uid") Integer uid, 
			@Param("avatar") String avatar,
			@Param("modifiedUser") String modifiedUser,
			@Param("modifiedTime") Date modifiedTime);

**c. 配置映射**

在`UserMapper.xml`中配置以上方法的映射：

在`UserMapperTests`中编写并执行单元测试：

### 21. 用户-上传头像-业务层

**a. 规划异常**

对于更新数据而言，必然可能出现`UpdateException`；

在更新之前，还需要检查用户数据，则可能出现`UserNotFoundException`。

以上异常类均已经存在，无需创建新的异常类。

**b. 接口与抽象方法**

在`IUserService`中添加抽象方法：

	/**
	 * 修改头像
	 * @param uid 用户的id
	 * @param username 用户名
	 * @param avatar 头像的路径
	 * @throws UserNotFoundException 用户数据不存在，或者已经被标记为删除
	 * @throws UpdateException 更新数据失败
	 */
	void changeAvatar(Integer uid, String username, String avatar) 
		throws UserNotFoundException, UpdateException;

**c. 实现类与重写方法**

在`UserServiceImpl`中重写以上方法：

	public void changeAvatar(
		Integer uid, String username, String avatar) 
			throws UserNotFoundException, UpdateException {
		// 根据参数uid查询用户数据
		// 判断查询结果是否为null
		// 抛出：UserNotFoundException
	
		// 判断查询结果中的isDelete为1
		// 抛出：UserNotFoundException
	
		// 创建当前时间对象
		// 执行更新头像，并获取返回的受影响的行数
		// 判断受影响的行数是否不为1
		// 抛出：UpdateException
	}

在`UserServiceTests`中编写并执行单元测试：

### 22. 用户-上传头像-控制器层

**a. 处理异常**

此次用户上传头像时，可能会出现以下问题：未选择上传的文件或文件大小为0字节；文件大小超出限制；文件类型超出允许的范围。

在上传过程中，存储文件时，调用的`transferTo()`方法会抛出2种异常：

	try {
		file.transferTo(dest);
	} catch (IllegalStateException e) {
		e.printStackTrace();
		throw new FileUploadStateException("文件传输错误!");
	} catch (IOException e) {
		e.printStackTrace();
		throw new FileUploadIOException("文件传输中断!");
	}

此时，不能直接将这2种异常再次抛出，因为，抛出的异常都会被方法的调用者处理，而当前控制器方法是会被SpringMVC框架调用的，框架本身并不会对这2种异常进行处理，所以，一旦出现异常，就会在页面中显示500错误，是极为不合适的！

针对这样的异常，应该在捕获这2个异常的时候，再次抛出自定义异常，并且在`BaseController`中处理自定义异常！

则应该创建异常类：

	com.loveoyh.store.controller.ex.ControllerException 继承自 RuntimeException
	
	com.loveoyh.store.controller.ex.FileUploadException 继承自 ControllerException
	
	com.loveoyh.store.controller.ex.FileEmptyException 继承自 FileUploadException
	com.loveoyh.store.controller.ex.FileSizeException 继承自 FileUploadException
	com.loveoyh.store.controller.ex.FileTypeException 继承自 FileUploadException
	
	com.loveoyh.store.controller.ex.FileUploadIOException 继承自 FileUploadException
	com.loveoyh.store.controller.ex.FileUploadStateException 继承自 FileUploadException

创建异常后，还应该在`BaseController`统一处理异常，首先需要调整处理哪些异常：

	@ExceptionHandler({ServiceException.class, ControllerException.class})
	
	...
	}else if(e instanceof FileEmptyException) {
	    //6000-文件为空异常类，例如没有选择文件或选择的文件为0字节的
	    jr.setState(6000);
	}else if(e instanceof FileSizeException) {
	    //6001-文件过大异常类，例如上传的文件大小超过规定的大小
	    jr.setState(6001);
	}else if(e instanceof FileTypeException) {
	    //6002-文件类型错误异常类，例如文件类型超出规定类型
	    jr.setState(6002);
	}else if(e instanceof FileUploadIOException) {
	    //6003-文件上传IO异常，例如：上传文件的读写问题
	    jr.setState(6003);
	}else if(e instanceof FileUploadStateException) {
	    //6004-文件上传状态异常类，例如：上传过程中源文件被移除，导致源文件找不到
	    jr.setState(6004);
	}
	...

**b. 设计请求**

	请求路径：/users/change_avatar
	请求参数：HttpServletRequest request, MultipartFile file
	请求类型：POST
	响应数据：JsonResult

**c. 处理请求**

	public static final int AVATAR_MAX_SIZE = 1 * 1024 * 1024;
	
	public static final List<String> AVATAR_CONTENT_TYPES = new ArrayList<>();
	
	public static final String AVATAR_DIR = "upload";
	
	static {
		AVATAR_CONTENT_TYPES.add("image/jpeg");
		AVATAR_CONTENT_TYPES.add("image/png");
	}
	
	@PostMapping("change_avatar")
	public JsonResult changeAvatar(
		HttpServletRequest request, 
		@RequestParam("file") MultipartFile file) {
		// 检查文件是否为空
		if (file.isEmpty()) {
			throw new FileEmptyException("文件为空!");
		}
	
		// 检查文件大小
		if (file.getSize() > AVATAR_MAX_SIZE) {
			throw new FileSizeException("文件过大!，不能超过"+(AVATAR_MAX_SIZE/1024)+"KB");
		}
	
		// 检查文件类型
		if (!AVATAR_CONTENT_TYPES.contains(file.getContentType())) {
			throw new FileTypeException("请使用以下图片类型："+AVATAR_CONTENT_TYPE);
		}
	
		// 确定文件夹
		String dirPath = request.getServletContext().getRealPath(AVATAR_DIR);
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	
		// 确定文件名
		String originalFilename = file.getOriginalFilename();
		String suffix = "";
		int beginIndex = originalFilename.lastIndexOf(".");
		if (beginIndex != -1) {
			suffix = originalFilename.substring(beginIndex);
		}
		String filename = UUID.randomUUID().toString() + suffix;
	
		// 执行保存
		File dest = new File(dir, filename);
		file.transferTo(dest);
	
		// 更新数据表
		String avatar = "/" + AVATAR_DIR + "/" + filename;
		HttpSession session = request.getSession();
		Integer uid = ...
		String username = ...
		userService.changeAvatar(uid, username, avatar);
	
		// 返回
		JsonResult jr = new JsonResult();
		jr.setState(SUCCESS);
		jr.setData(avatar);
		return jr;
	}

### 23. 用户-上传头像-前端页面

当使用jQuery的`$.ajax()`函数实现文件上传时，与普通的AJAX请求有2点区别：

1. 获取数据的方式：普通的请求可以通过`$("#form-id").serialize()`，而上传时需要使用`new FormData($("#form-id")[0])`；

2. 必须添加2项新的属性配置：`"contentType":false`和`"processData":false`



```text
注意：关于头像处理的代码调整

由于需要登录后就应该存储头像信息到客户端cookie中，有以下步骤：
1.在findByUsername查询是的SQL语句需要加上avatar字段，然后进行测试。因为在登录时应该通过此字段获得头像
2.在登录过程中ajax请求成功后需要将avatar保存到cookie里面
3.在修改头像后也需要将修改后的头像地址保存到cookie中的avatar中
```

### 24. 收货地址-分析

关于收货地址的管理，涉及的功能有：增加，查看列表，修改，删除，设为默认。

以上功能的开发顺序应该是：增加 > 查看列表 > 设为默认 > 删除 > 修改。

### 25. 收货地址-创建数据表

	CREATE TABLE o_address (
		aid INT AUTO_INCREMENT COMMENT '收货地址id',
		uid INT COMMENT '用户id',
		name VARCHAR(50) COMMENT '收货人姓名',
		province_code CHAR(6) COMMENT '省-代号',
		province_name VARCHAR(50) COMMENT '省-名称',
		city_code CHAR(6) COMMENT '市-代号',
		city_name VARCHAR(50) COMMENT '市-名称',
		area_code CHAR(6) COMMENT '区-代号',
		area_name VARCHAR(50) COMMENT '区-名称',
		zip CHAR(6) COMMENT '邮编',
		address VARCHAR(100) COMMENT '详细地址',
		phone VARCHAR(20) COMMENT '手机',
		tel VARCHAR(20) COMMENT '固话',
		tag VARCHAR(20) COMMENT '地址类型',
		is_default INT COMMENT '是否默认：0-非默认，1-默认',
		created_user VARCHAR(20) COMMENT '创建人',
		created_time DATETIME COMMENT '创建时间',
		modified_user VARCHAR(20) COMMENT '最后修改人',
		modified_time DATETIME COMMENT '最后修改时间',
		PRIMARY KEY (aid)
	) DEFAULT CHARSET=UTF8;

### 26. 收货地址-创建实体类

创建`com.loveoyh.store.entity.Address`继承自`BaseEntity`。

### 27. 收货地址-增加-持久层

**a. 规划SQL语句**

增加收货地址的SQL语句应该是：

	insert into o_address (除了aid以外的所有字段) values(匹配的实体类的属性值);

在增加时，还需要考虑当前增加的收货地址是不是当前用户的默认收货地址，可以设定规则“用户的第1条收货地址是默认的，后续增加的都不是默认的”，则可以查询该用户的收货地址数量，如果为0，则即将增加的就是第1条，就应该是默认的，如果不为0，表示用户已经有一些收货地址数据了，即将增加的不是第1条，则应该是非默认的！所以，需要“查询该用户的收货地址数量”的查询功能，对应的SQL语句应该是：

	select count(*) from o_address where uid=?

**b. 接口与抽象方法**

创建`com.loveoyh.store.mapper.AddressMapper`接口，并添加抽象方法：

	Integer insert(Address address);
	
	Integer countByUid(Integer uid);

**c. 配置映射**

复制得到`AddressMapper.xml`文件，确定根节点的`namespace`对应以上接口，然后，配置以上2个抽象方法的映射：

	<mapper namespace="com.loveoyh.store.mapper.AddressMapper">
		
		<!-- 插入收货地址数据 -->
		<!-- Integer insert(Address address) -->
		<insert id="insert"
			useGeneratedKeys="true"
			keyProperty="aid">
			INSERT INTO o_address (
				uid, name,
				province_code, province_name,
				city_code, city_name,
				area_code, area_name,
				zip, address,
				phone, tel,
				tag, is_default,
				created_user, created_time,
				modified_user, modified_time
			) VALUES (
				#{uid}, #{name},
				#{provinceCode}, #{provinceName},
				#{cityCode}, #{cityName},
				#{areaCode}, #{areaName},
				#{zip}, #{address},
				#{phone}, #{tel},
				#{tag}, #{isDefault},
				#{createdUser}, #{createdTime},
				#{modifiedUser}, #{modifiedTime}
			)
		</insert>
		
		<!-- 统计某个用户的收货地址数据的数量 -->
		<!-- Integer countByUid(Integer uid) -->
		<select id="countByUid"
			resultType="java.lang.Integer">
			SELECT 
				COUNT(*) 
			FROM 
				o_address 
			WHERE 
				uid=#{uid}
		</select>
		
	</mapper>

在**src/test/java**下创建`AddressMapperTests`单元测试类，添加测试相关注解，然后，编写并执行以上2个功能的单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class AddressMapperTests {
	
		@Autowired
		private AddressMapper mapper;
		
		@Test
		public void insert() {
			Address address = new Address();
			address.setUid(1);
			address.setName("赵六");
			Integer rows = mapper.insert(address);
			System.err.println("rows=" + rows);
			System.err.println("id=" + address.getAid());
		}
		
		@Test
		public void countByUid() {
			Integer uid = 10;
			Integer count = mapper.countByUid(uid);
			System.err.println("count=" + count);
		}
		
	}

### 28. 收货地址-增加-业务层

**a. 规划异常**

增加收货地址会执行INSERT操作，则可能抛出`InsertException`。

还可以设定规则，当收货地址的数量达到指定值后，不允许再创建，即每个用户最多只可以有X条收货地址，如果用户创建的收货地址达到上限后仍尝试继续创建，则可以抛出：`AddressCountLimitException`。

则需要创建`com.loveoyh.store.service.ex.AddressCountLimitException`。

**b. 接口与抽象方法**

创建`com.loveoyh.store.service.IAddressService`业务层接口，并添加抽象方法：

	void addnew(Address address, Integer uid, String username) throws AddressCountLimitException, InsertException;

并在接口中声明：

	int ADDRESS_MAX_COUNT = 20;

**c. 实现类与重写方法**

创建`com.loveoyh.store.service.impl.AddressServiceImpl`业务层实现类，实现以上接口，添加`@Service`注解，并在类中添加持久层`@Autowired private AddressMapper addressMppaer;`接口的对象：

	@Service
	public class AddressServiceImpl implements IAddressService {
	
		@Autowired
		private AddressMapper addressMppaer;
	
		public void addnew(Address address, Integer uid, String username) throws AddressCountLimitException, InsertException {
			// TODO 
		
		}
	}

在持久层中已经完成的增、删、改、查相关方法，每一个方法都有可能出现在多个业务中！在业务层调用时，可能都需要经过某些验证才可以保证正常使用，而多次调用，不应该多次编写验证的代码或复制这些代码，而应该把验证和持久层的功能封装在业务层中的某个方法中，以便于后续统一使用，例如，在业务层实现类中：

	private void insert(Address address) {
		Integer rows = addressMapper.insert(address);
		if (rows != 1) {
			throw new InsertException("增加收货地址失败！插入数据时出现未知错误！");
		}
	}
	
	private Integer countByUid(Integer uid) {
		Integer count = addressMapper.countByUid(uid);
		return count;
	}

所以，总的看来，在业务层实现类中，会把持久层接口中已有方法都添加一个同名的、私有的方法，并调用持久层对象来实现相关功能，添加相关验证和判断。

后续，在业务层的公有方法(重写的接口中的方法)中，都不会直接调用持久层对象来实现某种操作，而是调用自身的私有方法来实现。

然后，重写接口中的抽象方法：

	public void addnew(Address address, Integer uid, String username) throws AddressCountLimitException, InsertException {
		// 根据参数uid查询当前用户的收货地址数量
		// 判断收货地址数量是否达到上限值ADDRESS_MAX_COUNT
		// 是：抛出：AddressCountLimitException
	
		// 补全数据：uid
	
		// TODO 补全数据：province_name, city_name, area_name
	
		// 判断当前用户的收货地址数量是否为0，并决定is_default的值
		// 补全数据：is_default
	
		// 创建当前时间对象
		// 补全数据：4个日志
	
		// 插入收货地址数据
	}

具体实现为：

	@Override
	public void addnew(Address address, Integer uid, String username)
			throws AddressCountLimitException, InsertException {
		// 根据参数uid查询当前用户的收货地址数量
		Integer count = countByUid(uid);
		// 判断收货地址数量是否达到上限值ADDRESS_MAX_COUNT
		if (count >= ADDRESS_MAX_COUNT) {
			// 是：抛出：AddressCountLimitException
			throw new AddressCountLimitException(
				"增加收货地址失败！当前收货地址数量已经达到上限！最多允许创建" + ADDRESS_MAX_COUNT + "条，已经创建" + count + "条！");
		}
	
		// 补全数据：uid
		address.setUid(uid);
	
		// TODO 补全数据：province_name, city_name, area_name
	
		// 判断当前用户的收货地址数量是否为0，并决定is_default的值
		Integer isDefault = count == 0 ? 1 : 0;
		// 补全数据：is_default
		address.setIsDefault(isDefault);
	
		// 创建当前时间对象
		Date now = new Date();
		// 补全数据：4个日志
		address.setCreatedUser(username);
		address.setCreatedTime(now);
		address.setModifiedUser(username);
		address.setModifiedTime(now);
	
		// 插入收货地址数据
		insert(address);
	}

最后，在**src/test/java**下创建`com.loveoyh.store.service.AddressServiceTests`测试类，编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class AddressServiceTests {
		
		@Autowired
		IAddressService service;
		
		@Test
		public void addnew() {
			try {
				Integer uid = 2;
				String username = "系统管理员";
				Address address = new Address();
				address.setName("小孙同学");
				service.addnew(address, uid, username);
				System.err.println("OK.");
			} catch (ServiceException e) {
				System.err.println(e.getClass().getName());
				System.err.println(e.getMessage());
			}
		}
	
	}

### 29. 收货地址-增加-控制器层

**a. 处理异常**

此次需要处理`AddressCountLimitException`。

**b. 设计请求**

	请求路径：/addresses/addnew
	请求参数：Address address, HttpSession session
	请求类型：POST
	响应数据：JsonResult

**c. 处理请求**

创建`com.loveoyh.store.controller.AddressController`控制器类，继承自`BaseController`，添加`@RestController`和`@RequestMapping("addresses")`注解，并在类中添加业务层`@Autowired private IAddressService addressService;`接口的对象：

	@RestController
	@RequestMapping("addresses")
	public class AddressController extends BaseController {
	
		@Autowired 
		private IAddressService addressService;
	
	}

然后，在类中添加处理请求的方法：

	@RequestMapping("addnew")
	public JsonResult addnew(
		Address address, HttpSession session) {
		// 从Session中获取uid和username
		// 调用业务层对象执行增加
		// 响应成功
	}

完成后，打开浏览器，先登录，然后可以通过`http://localhost:8080/addresses/addnew?name=David`进行测试。

### 30. 收货地址-增加-前端页面

### 31. 省市区数据-导入数据

登录mysql控制台，通过以下指令导入：

	source ..../o_dict_district.sql

### 32. 创建实体类

创建`com.loveoyh.store.entity.District`实体类，由于字典表的数据不会被修改，所以，表中并没有4个日志字段，则实体类中也不需要，则该实体类不需要继承自`BaseEntity`，但是，仍需要实现`Serializable`接口：

	/**
	 * 省/市/区数据的实体类
	 */
	public class District implements Serializable {
	
		private static final long serialVersionUID = 3674364250490127944L;
	
		private Integer id;
		private String parent;
		private String code;
		private String name;
	
		// ...
	}

### 33. 省市区-获取列表-持久层

**a. 规划SQL语句**

在`o_dict_district`中记录了全国所有的省、市、区的数据，如果需要查询全国所有的省，或某个省所有市，或某个市所有区，需要执行的SQL语句是一样的，只不过参数不同而已：

	select * from o_dict_district where parent=?

**b. 接口与抽象方法**

创建`com.loveoyh.store.mapper.DistrictMapper`接口，然后指定抽象方法：

	List<District> findByParent(String parent);

**c. 配置映射**

复制得到`DistrictMapper.xml`文件，并配置以上抽象方法的映射：

	<mapper namespace="com.loveoyh.store.mapper.DistrictMapper">
		
		<!-- 根据父级代号获取全国所有省/某省所有市/某市所有区的列表 -->
		<!-- List<District> findByParent(String parent) -->
		<select id="findByParent"
			resultType="com.loveoyh.store.entity.District">
			SELECT
				id, parent,
				code, name
			FROM
				o_dict_district
			WHERE
				parent=#{parent}
			ORDER BY
				id
		</select>
		
	</mapper>

**注意：如果查询的结果中包含多条数据，一定要显式的指定排序规则，如果没有指定，则实际获取的数据将是没有顺序的（可能绝大部分时候会表现为按照id排序，事实上是根本没有顺序的）！**

然后，编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class DistrictMapperTests {
	
		@Autowired
		private DistrictMapper mapper;
		
		@Test
		public void findByParent() {
			String parent = "320100";
			List<District> list = mapper.findByParent(parent);
			System.err.println("BEGIN:");
			for (District item : list) {
				System.err.println(item);
			}
			System.err.println("END.");
		}
		
	}

### 34. 省市区-获取列表-业务层

**a. 规划异常**

无

**b. 接口与抽象方法**

创建`com.loveoyh.store.service.IDistrictService`接口，并添加抽象方法：

	/**
	 * 根据父级代号获取全国所有省/某省所有市/某市所有区的列表
	 * @param parent 父级代号，如果尝试获取全国所有省，则代号应该使用"86"
	 * @return 全国所有省/某省所有市/某市所有区的列表
	 */
	List<District> getByParent(String parent);

**c. 实现类与重写方法**

创建`com.loveoyh.store.service.impl.DistrictServiceImpl`业务层实现类，实现以上接口，添加`@Service`注解，并在类中添加持久层`@Autowired private DistrictMapper districtMppaer;`接口的对象。

复制`DistrictMapper`接口中的抽象方法，并私有化实现它：

	/**
	 * 根据父级代号获取全国所有省/某省所有市/某市所有区的列表
	 * @param parent 父级代号，如果尝试获取全国所有省，则代号应该使用"86"
	 * @return 全国所有省/某省所有市/某市所有区的列表
	 */
	private List<District> findByParent(String parent) {
		return districtMapper.findByParent(parent);
	}

然后，重写接口中定义的抽象方法：

	public List<District> getByParent(String parent) {
		return findByParent(parent);
	}

最后，编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class DistrictServiceTests {
	
		@Autowired
		private IDistrictService service;
		
		@Test
		public void getByParent() {
			String parent = "86";
			List<District> list = service.getByParent(parent);
			System.err.println("BEGIN:");
			for (District item : list) {
				System.err.println(item);
			}
			System.err.println("END.");
		}
		
	}

### 35. 省市区-获取列表-控制器层

**a. 处理异常**

无

**b. 设计请求**

	请求路径：/districts/
	请求参数：String parent
	请求类型：GET
	响应数据：JsonResult
	是否拦截：否，不拦截，需要在登录拦截器的配置中添加白名单

**c. 处理请求**

创建`com.loveoyh.store.controller.DistrictController`，继承自`BaseController`，添加`@RestController`和`@RequestMapping("districts")`注解，在类中声明`@Autowired private IDistrictService districtService;`业务层对象。

然后，在控制器类添加处理请求的方法：

	@RestController
	@RequestMapping("districts")
	public class DistrictController extends BaseController {
	
		@Autowired
		private IDistrictService districtService;
		
		@GetMapping("/")
		public JsonResult getByParent(String parent) {
			List<District> data = districtService.getByParent(parent);
			return new JsonResult(SUCCESS, data);
		}
		
	}

在com.loveoyh.store.conf.LoginInterceptorConfigurer类中添加白名单信息：

```java
...
excludePaths.add("/districts/");
...
```

完成后，打开浏览器，输入`http://localhost:8080/districts/?parent=86`进行测试。

### 36. 省市区-获取列表-前端页面

相应的javascript核心代码如下：

```javascript
$(document).ready(function(){
			$("#btn-addnew").click(function(){
				addAddress();
			});
			
			showProvinceList();
			
			$("#province-list").change(function(){
				showCityList();
				
				$("#area-list").html("<option seleted value=''>----请选择----</option>")
			});
			
			$("#city-list").change(function(){
				showAreaList();
			});
		});
		/** 请求展示区域列表 */
		function showAreaList(){
			let cityCode = $("#city-list").val();
			if(cityCode==0){
				return;
			}
			$.ajax({
				"url":"/districts/",
				"data":"parent="+cityCode,
				"type":"get",
				"dataType":"json",
				"success":function(data){
					if(data.state == 0){
						let lists = data.data;
						let str = "<option selected value='Null'>----请选择----</option>";
						for(let i=0;i<lists.length;i++){
							let temp = '<option value="'+lists[i].code+'">'+lists[i].name+'</option>';
							str += temp;
						}
						$("#area-list").html(str);
					}
				}
			});
		}
		/** 请求展示城市列表 */
		function showCityList(){
			let provinceCode = $("#province-list").val();
			if(provinceCode==0){
				return;
			}
			$.ajax({
				"url":"/districts/",
				"data":"parent="+provinceCode,
				"type":"get",
				"dataType":"json",
				"success":function(data){
					if(data.state == 0){
						let lists = data.data;
						let str = "<option selected value=''>----请选择----</option>";
						for(let i=0;i<lists.length;i++){
							let temp = '<option value="'+lists[i].code+'">'+lists[i].name+'</option>';
							str += temp;
						}
						$("#city-list").html(str);
					}
				}
			});
		}
		/** 请求展示省份列表 */
		function showProvinceList(){
			$("#city-list").append("<option selected value=''>----请选择----</option>");
			$("#area-list").append("<option selected value=''>----请选择----</option>");
			$.ajax({
				"url":"/districts/",
				"data":"parent=86",
				"type":"get",
				"dataType":"json",
				"success":function(data){
					if(data.state == 0){
						let lists = data.data;
						let str = "<option selected value=''>----请选择----</option>";
						for(let i=0;i<lists.length;i++){
							let temp = '<option value="'+lists[i].code+'">'+lists[i].name+'</option>';
							str += temp;
						}
						$("#province-list").html(str);
					}
				}
			});
		}
		/** 请求添加地址 */
		function addAddress(){
			$.ajax({
				"url":"/addresses/addnew",
				"data":$("#form-addnew").serialize(),
				"type":"post",
				"dataType":"json",
				"success":function(data){
					if(data.state == 0){
						alert("添加成功！");
						//跳转页面
					} else {
						alert(data.message);
					}
				},
				"error":function(data){
					alert(data.message);
				}
			});
		}
```

### 36. 收货地址-查看列表-持久层

**a. 规划SQL语句**

	select 
		tag,name,province_name,city_name,area_name,
		address,phone,aid,is_default
	from o_address 
	where uid=? 
	order by is_default desc, modified_time desc 

**b. 接口与抽象方法**

	List<Address> findByUid(Integer uid);

**c. 配置映射**

映射：

	<!-- 根据用户id查询该用户的收货地址列表 -->
	<!-- List<Address> findByUid(Integer uid) -->
	<select id="findByUid"
		resultType="com.loveoyh.store.entity.Address">
		SELECT
			aid, name,
			province_name AS provinceName,
			city_name AS cityName,
			area_name AS areaName,
			address, phone,
			is_default AS isDefault,
			tag
		FROM 
			o_address
		WHERE 
			uid=#{uid}
		ORDER BY
			is_default DESC,
			modified_time DESC
	</select>

测试：

	@Test
	public void findByUid() {
		Integer uid = 7;
		List<Address> list= mapper.findByUid(uid);
		System.err.println("BEGIN:");
		for (Address item : list) {
			System.err.println(item);
		}
		System.err.println("END.");
	}

### 37. 收货地址-查看列表-业务层

**a. 规划异常**

无

**b. 接口与抽象方法**

	List<Address> getByUid(Integer uid);

**c. 实现类与重写方法**

	public List<Address> getByUid(Integer uid) {
		return findByUid(uid);
	}
	
	privater List<Address> findByUid(Integer uid) {
		return addressMapper.findByUid(uid);
	}

测试：

	@Test
	public void getByUid() {
		Integer uid = 7;
		List<Address> list= service.getByUid(uid);
		System.err.println("BEGIN:");
		for (Address item : list) {
			System.err.println(item);
		}
		System.err.println("END.");
	}

### 38. 收货地址-查看列表-控制器层

**a. 处理异常**

无

**b. 设计请求**

	请求路径：/addresses/
	请求参数：HttpSession session
	请求类型：GET
	响应数据：JsonResult

**c. 处理请求**

	@GetMapping("/")
	public JsonResult getByUid(
			HttpSession session) {
		// 从session中获取uid
		Integer uid = getUidFromSession(session);
		// 调用业务层对象获取数据
		List<Address> data = addressService.getByUid(uid);
		// 响应
		return new JsonResult<>(data);
	}

### 39. 收货地址-查看列表-前端页面

核心js代码如下：

```javascript
		$(document).ready(function(){
			//显示用户现有信息
			showUserInfo();
		});
		function showUserInfo(){
			$.ajax({
				"url":"/addresses/",
				"data":null,
				"type":"get",
				"dataType":"json",
				"success":function(data){
					var list = data.data;
					
					$("#address-list").empty();
					for (var i = 0; i < list.length; i++) {
						let address = addressTemplate.replace('[tag]',list[i].tag);
						address = address.replace('[name]',list[i].name);
						address = address.replace('[address]',list[i].provinceName 											+list[i].cityName+list[i].address);
						address = address.replace('[phone]',list[i].phone);
						$("#address-list").append(address);
					}
					$(".add-def:eq(0)").hide();
				}
			});
		}
		
		var addressTemplate = '<tr>'
						+'<td>[tag]</td>'
						+'<td>[name]</td>'
						+'<td>[address]</td>'
						+'<td>[phone]</td>'
						+'<td><a class="btn btn-xs btn-info"><span class="fa fa-edit"></span> 修改</a></td>'
						+'<td><a class="btn btn-xs add-del btn-info"><span class="fa fa-trash-o"></span> 删除</a></td>'
						+'<td><a class="btn btn-xs add-def btn-default">设为默认</a></td>'
						+'</tr>';
```

### 40. 收货地址-设置默认-持久层

**a. 规划SQL语句**

如果要将某收货地址设置为默认，需要执行的SQL语句大致是：

	update o_address 
	set is_default=1, modified_user=?, modified_time=? 
	where aid=?

当把某收货地址设置为默认之前，还应该把原来的默认地址设置为非默认，可以选择将该用户的所有收货地址全部设置为非默认：

	update t_address 
	set is_default=0
	where uid=?

另外，在设置默认之前，还应该检查该收货地址数据是否存在，可以通过简单的查询来实现：

	select * from o_address where aid=?

并且，除了检查数据是否存在以外，还应该检查数据的归属是否正确，即该aid对应的数据是不是当前登录的用户的数据，则查询时，应该查出uid字段，与用户登录后Session中的uid进行对比，以判断数据归属，则查询需要调整为：

	select uid from o_address where aid=?

**b. 接口与抽象方法**

以上3个功能对应的抽象方法应该是：

	Integer updateNonDefault(Integer uid);
	
	Integer updateDefault(
		@Param("aid") Integer aid, 
		@Param("modifiedUser") String modifiedUser, 
		@Param("modifiedTime") Date modifiedTime);
	
	Address findByAid(Integer aid);

**c. 配置映射**

配置映射：

	<!-- 将某用户的所有收货地址设置为非默认 -->
	<!-- Integer updateNonDefault(Integer uid) -->
	<update id="updateNonDefault">
		UPDATE 
			o_address
		SET 
			is_default=0
		WHERE 
			uid=#{uid}
	</update>
	
	<!-- 将指定的收货地址设置为默认 -->
	<!-- Integer updateDefault(
	    @Param("aid") Integer aid, 
	    @Param("modifiedUser") String modifiedUser, 
	    @Param("modifiedTime") Date modifiedTime) -->
	<update id="updateDefault">
		UPDATE 
			o_address
		SET 
			is_default=1,
			modified_user=#{modifiedUser},
			modified_time=#{modifiedTime}
		WHERE 
			aid=#{aid}
	</update>
	
	<!-- 根据收货地址的数据id查询详情 -->
	<!-- Address findByAid(Integer aid) -->
	<select id="findByAid"
		resultType="com.loveoyh.store.entity.Address">
		SELECT
			uid
		FROM 
			o_address
		WHERE 
			aid=#{aid}
	</select>

单元测试：

	@Test
	public void updateNonDefault() {
		Integer uid = 7;
		Integer rows = mapper.updateNonDefault(uid);
		System.err.println("rows=" + rows);
	}
	
	@Test
	public void updateDefault() {
		Integer aid = 25;
		String modifiedUser = "超级管理员";
		Date modifiedTime = new Date();
		Integer rows = mapper.updateDefault(aid, modifiedUser, modifiedTime);
		System.err.println("rows=" + rows);
	}
	
	@Test
	public void findByAid() {
		Integer aid = 25;
		Address result = mapper.findByAid(aid);
		System.err.println(result);
	}

### 41. 收货地址-设置默认-业务层

**a. 规划异常**

此次操作的流程大致是：先检查收货地址数据是否存在，再检查数据归属是否正确，然后再执行修改操作。

检查数据是否存在时，可能抛出：`AddressNotFoundException`；

检查数据归属是否正确时，可能抛出：`AccessDeniedException`；

执行修改操作时，可能抛出：`UpdateException`。

所以，需要事先创建原本不存在的异常类。

**b. 接口与抽象方法**

在`AddressService`接口中添加抽象方法：

	void setDefault(Integer aid, Integer uid, String username) throws AddressNotFoundException, AccessDeniedException, UpdateException;

**c. 实现类与重写方法**

首先，将持久层的3个新增方法复制到`AddressServiceImpl`业务层实现类中：

	Integer updateNonDefault(Integer uid);
	
	Integer updateDefault(Integer aid, String modifiedUser, Date modifiedTime);
	
	Address findByAid(Integer aid);

未实现的方法是报错的，需要先私有化实现：

	private void updateNonDefault(Integer uid) throws UpdateException {
		Integer rows = addressMapper.updateNonDefault(uid);
		if (rows == 0) {
			throw new UpdateException("...");
		}
	}
	
	private void updateDefault(Integer aid, String modifiedUser, Date modifiedTime) throw UpdateException {
		Integer rows = addressMapper.updateDefault(aid, modifiedUser, modifiedTime);
		if (rows != 1) {
			throw new UpdateException("...");
		}
	}
	
	private Address findByAid(Integer aid) {
		return addressMapper.findByAid(aid);
	}

然后，重写接口中定义的抽象方法：

	public void setDefault(Integer aid, Integer uid, String username) throws AddressNotFoundException, AccessDeniedException, UpdateException {
		// 根据aid查询收货地址数据
		// 判断结果是否为null
		// 是：抛出AddressNotFoundException
	
		// 判断结果中的uid与参数uid是否不一致
		// 是：抛出AccessDeniedException
	
		// 将该用户所有收货地址设置为非默认
	
		// 将指定的收货地址设置为默认
	}

具体实现为：

	@Override
	@Transactional
	public void setDefault(Integer aid, Integer uid, String username)
			throws AddressNotFoundException, AccessDeniedException, UpdateException {
		// 根据aid查询收货地址数据
		Address result = findByAid(aid);
	    // 判断结果是否为null
		if (result == null) {
			// 是：抛出AddressNotFoundException
			throw new AddressNotFoundException(
				"设置默认收货地址失败！尝试操作的数据不存在！");
		}
	
	    // 判断结果中的uid与参数uid是否不一致
		if (result.getUid() != uid) {
			// 是：抛出AccessDeniedException
			throw new AccessDeniedException(
				"设置默认收货地址失败！不允许访问他人的数据！");
		}
	
	    // 将该用户所有收货地址设置为非默认
		updateNonDefault(uid);
	
	    // 将指定的收货地址设置为默认
		updateDefault(aid, username, new Date());
	}

完成后，编写并执行单元测试：

	@Test
	public void setDefault() {
		try {
			Integer aid = 36;
			Integer uid = 7;
			String username = "悟空";
			service.setDefault(aid, uid, username);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

### 42. 收货地址-设置默认-控制器层

**a. 处理异常**

需要处理`AddressNotFoundException`和`AccessDeniedException`。

**b. 设计请求**

	请求路径：/addresses/{aid}/set_default
	请求参数：@PathVariable("aid") Integer aid, HttpSession session
	请求类型：POST
	响应数据：JsonResult

**c. 处理请求**

	@RequestMapping("{aid}/set_default")
	public JsonResult setDefault(
		@PathVariable("aid") Integer aid, HttpSession session) {
		// 从Session中获取uid和username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		// 调用业务层对象执行设置默认
		addressService.setDefault(aid, uid, username);
		// 响应成功
		return new JsonResult<>(SUCCESS); 
	}

### 43. 收货地址-设置默认-前端页面

核心jquery代码：

```javascript
		$(document).ready(function(){
			//显示用户现有信息
			showUserInfo();
			
			//绑定设置默认收货地址点击按钮
			$("#address-list").on("click",".add-def",setDefault);
		});		
		/** 展示用户收货地址信息 */
		function showUserInfo(){
			...
		}
		/** 设置默认收货地址 */
		function setDefault(e){
			let aBtn = e.target;
			let aid = $(aBtn).parent().parent().data("aid");
			$.ajax({
				"url":"/addresses/"+aid+"/set_default",
				"data":null,
				"type":"POST",
				"dataType":"json",
				"success":function(data){
					if(data.state == 0){
						showUserInfo();
					}
				},
				"error":function(){
					alert("login timeout!");
				}
			});
		}
```

### 44. 收货地址-删除-持久层

**a. 规划SQL语句**

删除某收货地址数据的SQL语句应该是：

	delete from o_address where aid=?

如果删除的数据是默认的收货地址，还需要考虑将另外的哪一条设置为默认，则，首先需要能够判断刚删除的是不是默认的，当然，在删除之前，还是应该检查数据是否存在、数据归属问题，是已经通过查询操作实现了，可以调整该查询功能，补充查询`is_default`字段的值，以适用于判断“即将删除的数据是不是默认收货地址”，则：

	select uid, is_default from o_address where aid=?

当删除的是默认收货地址后，也有可能刚刚删除的这条是该用户的最后一条地址，也不需要有后续操作，通过原有的`countByUid()`功能即可实现该判断，无需重新开发功能。

当删除的是默认收货地址，且后续还有超过0条数据，则应该考虑将剩余的某条数据设置为默认，规则可以自由决定，例如“将最近修改(含创建)过的那条收货地址设置为默认”，通过查询该用户的数据，并根据最后修改时间进行排序，取出第1条即可：

	select aid from o_address where uid=? order by modified_time desc limit 0,1

最后，根据查询结果的aid将这条数据设置为默认即可！

所以，关于“删除”功能的开发顺序大致是：

1. 检查数据是否存在，数据归属的问题；

2. 执行删除；

3. 判断刚才删除的是否是默认地址；

4. 判断刚才删除的是否是最后一条地址/判断当前是否还有收货地址；

5. 找出最近修改的收货地址，并设置为默认。

**b. 接口与抽象方法**

执行删除时，需要执行抽象方法：

	Integer deleteByAid(Integer aid);

查询最近修改的收货地址的抽象方法：

	Address findLastModified(Integer uid);

**c. 配置映射**

找到`findByAid()`方法的映射，补充查询`is_default`字段；

然后，配置以上2个新的抽象方法的映射：

	<!-- 根据收货地址id删除数据 -->
	<!-- Integer deleteByAid(Integer aid) -->
	<delete id="deleteByAid">
		DELETE FROM 
			o_address
		WHERE
			aid=#{aid}
	</delete>
	
	<!-- 查询某用户最后一次修改的收货地址数据 -->
	<!-- Address findLastModified(Integer uid) -->
	<select id="findLastModified"
		resultType="com.loveoyh.store.entity.Address">
		SELECT
			aid
		FROM 
			o_address
		WHERE 
			uid=#{uid}
		ORDER BY
			modified_time DESC
		LIMIT 0,1
	</select>

然后，编写并执行单元测试：
	

	@Test
	public void deleteByAid() {
		Integer aid = 22;
		Integer rows = mapper.deleteByAid(aid);
		System.err.println("rows=" + rows);
	}
	
	@Test
	public void findLastModified() {
		Integer uid = 7;
		Address result = mapper.findLastModified(uid);
		System.err.println(result);
	}

### 45. 收货地址-删除-业务层

**a. 规划异常**

关于“删除”功能的开发顺序大致是：

1. 检查数据是否存在，数据归属的问题；

2. 执行删除；

3. 判断刚才删除的是否是默认地址；

4. 判断刚才删除的是否是最后一条地址/判断当前是否还有收货地址；

5. 找出最近修改的收货地址，并设置为默认。

检查数据是否存在，可能抛出：`AddressNotFoundException`；

检查数据归属是否正确，可能抛出：`AccessDeniedException`；

执行删除，可能抛出：`DeleteException`；

执行设置为默认，可能抛出：`UpdateException`。

则需要创建`com.loveoyh.store.service.ex.DeleteException`。

**b. 接口与抽象方法**

在`AddressService`中添加抽象方法：

	void delete(Integer aid, Integer uid, String username) throws AddressNotFoundException, AccessDeniedException, DeleteException, UpdateException;

**c. 实现类与重写方法**

在`AddressServiceImpl`实现类中，首先，私有化实现持久层中新增的2个方法：

然后，重写`AddressService`接口中定义的抽象方法：

	@Transactional
	public void delete(Integer aid, Integer uid, String username) throws AddressNotFoundException, AccessDeniedException, DeleteException, UpdateException {
		// 根据aid查询收货地址数据
		// 判断结果是否为null
		// 是：抛出AddressNotFoundException
	
		// 判断结果中的uid与参数uid是否不一致
		// 是：抛出AccessDeniedException
	
		// 执行删除
	
		// 判断此前的查询结果中的isDefault是否为0
		// return;
	
		// 统计当前用户的收货地址数量：countByUid()
		// 判断剩余收货地址数量是否为0
		// return;
	
		// 查询当前用户最近修改的收货地址
		// 将最近修改的收货地址设置为默认
	}

具体实现为：

	@Override
	@Transactional
	public void delete(Integer aid, Integer uid, String username)
			throws AddressNotFoundException, AccessDeniedException, DeleteException, UpdateException {
		// 根据aid查询收货地址数据
		Address result = findByAid(aid);
	    // 判断结果是否为null
		if (result == null) {
			// 是：抛出AddressNotFoundException
			throw new AddressNotFoundException(
				"删除收货地址失败！尝试操作的数据不存在！");
		}
	
	    // 判断结果中的uid与参数uid是否不一致
		if (result.getUid() != uid) {
			// 是：抛出AccessDeniedException
			throw new AccessDeniedException(
				"删除收货地址失败！不允许访问他人的数据！");
		}
	
	    // 执行删除
		deleteByAid(aid);
	
	    // 判断此前的查询结果中的isDefault是否为0
		if (result.getIsDefault() == 0) {
			// return;
			return;
		}
	
	    // 统计当前用户的收货地址数量：countByUid()
		Integer count = countByUid(uid);
	    // 判断剩余收货地址数量是否为0
		if (count == 0) {
			// return;
			return;
		}
	
	    // 查询当前用户最近修改的收货地址
		Address lastModifiedAddress = findLastModified(uid);
	    // 将最近修改的收货地址设置为默认
		updateDefault(lastModifiedAddress.getAid(), username, new Date());
	}

单元测试：

	@Test
	public void delete() {
		try {
			Integer aid = 27;
			Integer uid = 7;
			String username = "悟空";
			service.delete(aid, uid, username);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

### 46. 收货地址-删除-控制器层

**a. 处理异常**

需要处理`DeleteException`。

**b. 设计请求**

	请求路径：/addresses/{aid}/delete
	请求参数：@PathVariable("aid") Integer aid, HttpSession session
	请求类型：POST
	响应数据：JsonResult

**c. 处理请求**

	@RequestMapping("{aid}/delete")
	public JsonResult delete(@PathVariable("aid") Integer aid,HttpSession session){
	    String username = getUsernameFromSession(session);
	    Integer uid = getUidFromSession(session);
	    addressService.delete(aid, uid, username);
	    return new JsonResult();
	}

### 47. 收货地址-删除-前端页面

核心javascript代码：

```javascript
		$(document).ready(function(){
			//显示用户现有信息
			showUserInfo();
			
			//绑定设置默认收货地址点击按钮
			$("#address-list").on("click",".add-def",setDefault);
			
			//绑定删除收货地址点击按钮
			$("#address-list").on("click",".add-del",deleteAddress);
		});

		/** 删除收货地址 */
		function deleteAddress(e){
			let aBtn = e.target;
			let aid = $(aBtn).parent().parent().data("aid");
			$.ajax({
				"url":"/addresses/"+aid+"/delete",
				"data":null,
				"type":"POST",
				"dataType":"json",
				"success":function(data){
					if(data.state == 0){
						showUserInfo();
					}
				},
				"error":function(){
					alert("login timeout!");
				}
			});
		}
```

### 48. 主页-显示热销排行列表-实体类

创建`com.loveoyh.store.entity.Goods`商品数据的实体类，继承自`BaseEntity`：

### 49. 主页-显示热销排行列表-持久层

查询4条热销商品的SQL语句大致是：

	select * from o_goods where state=1 and num>100 order by priority desc limit 0,4;

创建`com.loveoyh.store.mapper.GoodsMapper`处理商品数据的持久层接口，并添加抽象方法：

	List<Goods> findHotList();

然后复制得到`GoodsMapper.xml`文件，修改根节点的`namespace`对应以上接口，然后配置以上方法的映射：

	<mapper namespace="com.loveoyh.store.mapper.GoodsMapper">
		
		<!-- 获取热销商品列表 -->
		<!-- List<Goods> findHotList() -->
		<select id="findHotList"
			resultType="com.loveoyh.store.entity.Goods">
			SELECT
				id, title,
				price, image
			FROM
				o_goods
			WHERE
				status=1 AND num>100
			ORDER BY
				priority DESC
			LIMIT 0,4
		</select>
		
	</mapper>

最后，在**src/test/java**下创建`com.loveoyh.store.mapper.GoodsMapperTests`测试类，编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class GoodsMapperTests {
	
		@Autowired
		private GoodsMapper mapper;
		
		@Test
		public void findHotList() {
			List<Goods> list = mapper.findHotList();
			System.err.println("BEGIN:");
			for (Goods item : list) {
				System.err.println(item);
			}
			System.err.println("END.");
		}
		
	}

### 50. 主页-显示热销排行列表-业务层

查询并显示列表类的操作没有异常！

创建`com.loveoyh.store.service.GoodsService`业务层接口，并添加抽象方法：

	/**
	 * 获取热销商品列表
	 * @return 热销商品列表
	 */
	List<Goods> getHotList();

然后，创建`com.loveoyh.store.service.impl.GoodsServiceImpl`业务层实现类，实现以上接口，添加`@Service`注解，并在类中添加声明`@Autowired private GoodsMapper goodsMapper;`持久层对象：

	@Service
	public class GoodsServiceImpl implements IGoodsService {
		
		@Autowired
		private GoodsMapper goodsMapper;
	
	}

在实现时，先将持久层的方法再次复制过来，并私有化实现：

	/**
	 * 获取热销商品列表
	 * @return 热销商品列表
	 */
	private List<Goods> findHotList() {
		return goodsMapper.findHotList();
	}

然后实现接口中抽象方法，调用以上私有方法即可实现：

	@Override
	public List<Goods> getHotList() {
		return findHotList();
	}

在**src/test/java**下创建`com.loveoyh.store.service.GoodsServiceTests`单元测试类，编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class GoodsServiceTests {
	
		@Autowired
		private IGoodsService service;
		
		@Test
		public void getHotList() {
			List<Goods> list = service.getHotList();
			System.err.println("BEGIN:");
			for (Goods item : list) {
				System.err.println(item);
			}
			System.err.println("END.");
		}
		
	}

### 51. 主页-显示热销排行列表-控制器

此次不需要添加对异常的处理！

此次需要新创建`com.loveoyh.store.controller.GoodsController`控制器类，继承自`BaseController`，添加`@RestController`和`@RequestMapping("goods")`注解，并在控制器类中声明`@Autowired private IGoodsService goodsService;`业务层对象：

	@RestController
	@RequestMapping("goods")
	public class GoodsController extends BaseController {
	
		@Autowired
		private IGoodsService goodsService;
	
	}

然后，在控制器类中添加处理请求的方法：

	@GetMapping("hot")
	public JsonResult getHotList() {
		// 调用业务层对象获取数据
		// 返回
	}

关于商品数据的访问，都应该是不要求登录的，则应该在登录拦截器的配置中添加白名单：`/goods/**`、`/web/index.html`、`/web/product.html`。

全部完成后，可通过`http://localhost:8080/goods/hot`进行测试访问。

### 51. 主页-显示热销排行列表-前端界面

核心javascript代码：

```javascript
		$(document).ready(function(){
			//显示热销商品
			showHotGoodsList();
		});
		
		/** 展示热销商品列表 */
		function showHotGoodsList(){
			$.ajax({
				"url":"/goods/hot",
				"data":null,
				"type":"get",
				"dataType":"json",
				"success":function(data){
					var list = data.data;
					
					$("#hot-list").empty();
					for (var i = 0; i < list.length; i++) {
						let goods = goodsTemplate.replace('[title]',list[i].title);
						goods = goods.replace('[price]',list[i].price);
						goods = goods.replace('[image]',list[i].image);
						goods = goods.replace('[id]',list[i].id);
						goods = $(goods);
						$("#hot-list").append(goods);
					}
				}
			});
		}
		
		/** 商品模板 */
		var goodsTemplate = '<div class="col-md-12">'
			+'<div class="col-md-7 text-row-2"><a href="product.html?id=[id]">[title]</a></div>'
			+'<div class="col-md-2">[price]</div>'
			+'<div class="col-md-3"><img src="..[image]collect.png" class="img-responsive" /></div>'
			+'</div>';
```

### 53. 显示商品详情-持久层

显示商品详情的SQL语句大致是：

	select title,sell_point,price,image,status,num from o_goods where id=?

然后在接口中添加抽象方法：

	Goods findById(Long id);

然后配置该抽象方法的映射：

	<!-- 根据商品id查询商品详情 -->
	<!-- Goods findById(Long id) -->
	<select id="findById"
		resultType="com.loveoyh.store.entity.Goods">
		SELECT
			title,
			sell_point AS sellPoint,
			price, image,
			status, num
		FROM
			o_goods
		WHERE
			id=#{id}
	</select>

最后，编写并执行单元测试：

	@Test
	public void findById() {
		Long id = 10000042L;
		Goods result = mapper.findById(id);
		System.err.println(result);
	}

### 54. 显示商品详情-业务层

查询操作可以不涉及异常。

在`IGoodsService`中添加抽象方法：

	Goods getById(Long id);

然后在`GoodsServiceImpl`中粘贴持久层的抽象方法，将其私有化实现：

	/**
	 * 根据商品id查询商品详情
	 * @param id 商品id
	 * @return 匹配的商品详情，如果没有匹配的数据，则返回null
	 */
	private Goods findById(Long id) {
		return goodsMapper.findById(id);
	}

再重写接口中的抽象方法，调用自身的私有方法实现功能：

	@Override
	public Goods getById(Long id) {
		return findById(id);
	}

最后，编写并执行单元测试：

	@Test
	public void getById() {
		Long id = 10000042L;
		Goods result = service.getById(id);
		System.err.println(result);
	}

### 55. 显示商品详情-控制器层

由于业务层没有抛出新的异常，则控制器层也不需要处理异常。

需要在`GoodsController`中添加处理请求的方法：

	@GetMapping("{id}/details")
	public JsonResult getById(
		@PathVariable("id") Long id) {
		// 调用业务层对象获取数据
		Goods data = goodsService.getById(id);
		// 返回
		return new JsonResult<>(SUCCESS, data);
	}

完成后，通过`http://localhost:8080/goods/10000042/details`进行测试。

### 56. 显示商品详情-前端界面

核心javascript代码：

```javascript
		//自定义封装函数（getUrlParam）
		let id = $.getUrlParam("id");

		$(document).ready(function(){
			//显示热销商品详情
			showGoodsDetail(id);
		});
		
		/** 获取并展示热销商品详情 */
		function showGoodsDetail(id){
			$.ajax({
				"url":"/goods/"+id+"/details",
				"data":null,
				"type":"get",
				"dataType":"json",
				"success":function(data){
					var goodsData = data.data;
					
					$("#goods-detail").empty();
					let goodsDetail = goodsDetailTemplate.replace(/\[title\]/g,goodsData.title);
					goodsDetail = goodsDetail.replace(/\[price\]/g,goodsData.price);
					goodsDetail = goodsDetail.replace(/\[sell-point\]/g,goodsData.sellPoint);
					goodsDetail = goodsDetail.replace(/\[image\]/g,goodsData.image);
					goodsDetail = $(goodsDetail);
					$("#goods-detail").append(goodsDetail);
					
					handleImage();
				}
			});
		}

		/** 处理图片 */
		function handleImage() {
            ...//在其他文件中，由于ajax异步请求，需要再次执行才生效
        }
```

### 56. 购物车-加入购物车-创建数据表

	CREATE TABLE o_cart (
		cid INT AUTO_INCREMENT COMMENT '购物车数据id',
		uid INT NOT NULL COMMENT '用户id',
		gid BIGINT(20) NOT NULL COMMENT '商品id',
		num INT NOT NULL COMMENT '商品数量',
		created_user VARCHAR(20) COMMENT '创建人',
		created_time DATETIME COMMENT '创建时间',
		modified_user VARCHAR(20) COMMENT '最后修改人',
		modified_time DATETIME COMMENT '最后修改时间',
		PRIMARY KEY (cid)
	) DEFAULT CHARSET=UTF8;

### 57. 购物车-加入购物车-创建实体类

创建`com.loveoyh.store.entity.Cart`实体类，继承自`BaseEntity`：

### 58. 购物车-加入购物车-持久层

将商品添加到购物车，需要执行的SQL语句大致是：

	INSERT INTO o_cart (除了cid以外的字段列表) VALUES (值列表);

需要注意的是，并不是每次点击“加入购物车”都会产生新的数据，例如：7号用户已经把1号商品添加到购物车，数量为2，数据如下：

	uid		gid		num
	7		1		2

如果此时，7号用户再次点击1号商品界面中的“加入购物车”，添加1个到购物车中，其实，应该只修改原有数据的数量，并不会产生新的数据，如下：

	uid		gid		num
	7		1		3
	7		2		1
	8		1		1

所以，“加入购物车”功能还可能执行：

	UPDATE o_cart SET num=?, modified_user=?, modified_time=? WHERE cid=?

每次用户点击“加入购物车”时，到底执行INSERT操作，还是UPDATE操作，取决于“该用户的购物车中有没有该商品”，实现该判断的SQL查询语句大致是：

	SELECT * FROM o_cart WHERE uid=? AND gid=?

执行以上查询时，如果查询到有效结果，则表示“该用户的购物车中已有该商品”，反之，则表示“该用户的购物车中没有该商品”；如果查询到有效结果，后续将执行UPDATE操作，即：更新购物车中商品的数量，则需要将原数量读出来，用于结合用户此次提交的增量，计算新的数量，并且，还需要读取出这条数据的id，便于执行UPDATE操作，则以上查询应该调整为：

	SELECT cid, num FROM o_cart WHERE uid=? AND gid=?

分析完成！然后创建`com.loveoyh.store.mapper.CartMapper`持久层接口，并添加以上3个功能对应的抽象方法：

	Integer insert(Cart cart);
	
	Integer updateNum(
		@Param("cid") Integer cid, 
		@Param("num") Integer num, 
		@Param("modifiedUser") String modifiedUser, 
		@Param("modifiedTime") Date modifiedTime);
	
	Cart findByUidAndGid(
		@Param("uid") Integer uid,
		@Param("gid") Long gid);

复制得到`CartMapper.xml`，修改根节点的`namespace`属性的值，并配置以上3个抽象方法的映射：

	<mapper namespace="com.loveoyh.store.mapper.CartMapper">
		
		<!-- 插入购物车数据 -->
		<!-- Integer insert(Cart cart) -->
		<insert id="insert"
			useGeneratedKeys="true"
			keyProperty="cid">
			INSERT INTO o_cart (
			    uid, gid,
			    num,
			    created_user, created_time,
			    modified_user, modified_time
			) VALUES (
				#{uid}, #{gid},
			    #{num},
			    #{createdUser}, #{createdTime},
			    #{modifiedUser}, #{modifiedTime}
			)
		</insert>
		
		<!-- 修改购物车中商品的数量 -->
		<!-- Integer updateNum(
		    @Param("cid") Integer cid, 
		    @Param("num") Integer num, 
		    @Param("modifiedUser") String modifiedUser, 
		    @Param("modifiedTime") Date modifiedTime) -->
		<update id="updateNum">
			UPDATE
				o_cart
			SET
				num=#{num},
				modified_user=#{modifiedUser},
				modified_time=#{modifiedTime}
			WHERE
				cid=#{cid}
		</update>
		
		<!-- 根据用户id和商品id查询购物车数据 -->
		<!-- Cart findByUidAndGid(
		    @Param("uid") Integer uid,
		    @Param("gid") Long gid) -->
		<select id="findByUidAndGid"
			resultType="com.loveoyh.store.entity.Cart">
			SELECT
				cid, num
			FROM
				o_cart
			WHERE
				uid=#{uid} AND gid=#{gid}
		</select>
		
	</mapper>

在**src/test/java**下创建`com.loveoyh.store.mapper.CartMapperTests`测试类，测试以上3个方法：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class CartMapperTests {
	
		@Autowired
		private CartMapper mapper;
		
		@Test
		public void insert() {
			Cart cart = new Cart();
			cart.setUid(7);
			cart.setGid(1L);
			cart.setNum(2);
			Integer rows = mapper.insert(cart);
			System.err.println("rows=" + rows);
		}
		
		@Test
		public void updateNum() {
			Integer cid = 1;
			Integer num = 5;
			String modifiedUser = "管理员";
			Date modifiedTime = new Date();
			Integer rows = mapper.updateNum(cid, num, modifiedUser, modifiedTime);
			System.err.println("rows=" + rows);
		}
		
		@Test
		public void findByUidAndGid() {
			Integer uid = 7;
			Long gid = 1L;
			Cart result = mapper.findByUidAndGid(uid, gid);
			System.err.println(result);
		}
		
	}

### 59. 购物车-加入购物车-业务层

此次业务层将调用持久层开发的3个方法，其中，查询的方法不会抛出异常，因为无论是否查询到数据，此次操作都是正确的；而插入数据和更新数据将有可能分别抛出`InsertException`和`UpdateException`。

创建`com.loveoyh.store.service.CartService`业务层接口，并添加抽象方法：

	void addToCart(Cart cart, Integer uid, String username) throws InsertException, UpdateException;

创建`com.loveoyh.store.service.impl.CartServiceImpl`业务层实现类，实现以上接口，添加`@Service`注解，在类中声明`@Autowired private CartMapper cartMapper;`持久层对象：

	@Service
	public class CartServiceImpl implements CartService {
	
		@Autowired 
		private CartMapper cartMapper;
	
	}

然后，私有化实现持久层中的3个抽象方法：

	/**
	 * 插入购物车数据
	 * @param cart 购物车数据
	 * @throws InsertException 插入数据异常
	 */
	private void insert(Cart cart) throws InsertException {
		Integer rows = cartMapper.insert(cart);
		if (rows != 1) {
			throw new InsertException(
				"将商品添加到购物车失败！插入数据时出现未知错误！");
		}
	}
	
	/**
	 * 修改购物车中商品的数量
	 * @param cid 购物车数据的id
	 * @param num 新的商品数量
	 * @param modifiedUser 修改执行人
	 * @param modifiedTime 修改时间
	 * @throws UpdateException 更新数据异常
	 */
	private void updateNum(Integer cid, Integer num, 
	    String modifiedUser, Date modifiedTime)
			throws UpdateException {
		Integer rows = cartMapper.updateNum(cid, num, modifiedUser, modifiedTime);
		if (rows != 1) {
			throw new UpdateException(
				"更新商品数量失败！更新数据时出现未知错误！");
		}
	}
	
	/**
	 * 根据用户id和商品id查询购物车数据
	 * @param uid 用户id
	 * @param gid 商品id
	 * @return 匹配的购物车数据，如果没有匹配的数据，则返回null
	 */
	private Cart findByUidAndGid(Integer uid, Long gid) {
		return cartMapper.findByUidAndGid(uid, gid);
	}

最后，重写接口中抽象方法：

	public void addToCart(Cart cart, Integer uid, String username) throws InsertException, UpdateException {
		// 创建时间对象
		// 根据参数cart中封装的uid和gid执行查询
		// 检查查询结果是否为null
		// 是：
		// -- 基于参数uid向参数cart中封装uid
		// -- 基于参数username向参数cart中封装createdUser和modifiedUser
		// -- 向参数cart中封装createdTime和modifiedTime
		// -- 执行插入数据
		// 否：updateNum(cid, num, modifiedUser, modifiedTime);
		// -- 从查询结果中获取cid
		// -- 从查询结果中获取num，它是商品的原数量
		// -- 将以上获取的原数量与参数cart中的num相加，得到新的数量
		// -- 执行修改数量
	}

具体实现为：

	@Override
	public void addToCart(Cart cart, Integer uid, String username) throws InsertException, UpdateException {
		// 创建时间对象
		Date now = new Date();
		
		// 根据参数cart中封装的uid和gid执行查询
		Cart result = findByUidAndGid(uid, cart.getGid());
		// 检查查询结果是否为null
		if (result == null) {
			// 是：
			// 基于参数uid向参数cart中封装uid
			cart.setUid(uid);
			// 基于参数username向参数cart中封装createdUser和modifiedUser
			cart.setCreatedUser(username);
			cart.setModifiedUser(username);
			// 向参数cart中封装createdTime和modifiedTime
			cart.setCreatedTime(now);
			cart.setModifiedTime(now);
			// 执行插入数据
			insert(cart);
		} else {
			// 否：updateNum(cid, num, modifiedUser, modifiedTime);
			// 从查询结果中获取cid
			Integer cid = result.getCid();
			// 从查询结果中获取num，它是商品的原数量
			Integer oldNum = result.getNum();
			// 将以上获取的原数量与参数cart中的num相加，得到新的数量
			Integer newNum = oldNum + cart.getNum();
			// 执行修改数量
			updateNum(cid, newNum, username, now);
		}
	}

完成后，在**src/test/java**中创建`com.loveoyh.store.service.CartServiceTests`测试类，编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class CartServiceTests {
	
		@Autowired
		private ICartService service;
		
		@Test
		public void addToCart() {
			try {
				Cart cart = new Cart();
				cart.setGid(10L);
				cart.setNum(3);
				Integer uid = 70;
				String username = "购物卡车";
				service.addToCart(cart, uid, username);
				System.err.println("OK.");
			} catch (ServiceException e) {
				System.err.println(e.getClass().getName());
				System.err.println(e.getMessage());
			}
		}
		
	}

### 60. 购物车-加入购物车-控制器层

此次业务层并没有抛出新的异常，则无需处理异常。

创建`com.loveoyh.store.controller.CartController`控制器类，继承自`BaseController`，添加`@RestController`和`@RequestMapping("carts")`注解，并在类中声明`@Autowired private CartService cartService;`业务层对象：

	@RestController
	@RequestMapping("carts")
	public class CartController extends BaseController {
	
		@Autowired 
		private ICartService cartService;
	
	}

然后，在控制器类中添加处理请求的方法：

	@RequestMapping("add_to_cart")
	public JsonResult addToCart(Cart cart, HttpSession session) {
		// 从Session中获取uid和username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		// 调用业务层对象执行加入购物车
		cartService.addToCart(cart, uid, username);
		// 响应成功
		return new JsonResult<>(SUCCESS);
	}

最后，打开浏览器，先登录，然后通过`http://localhost:8080/carts/add_to_cart?gid=50&num=3`进行测试。

### 61. 购物车-加入购物车-前端界面

核心js代码:

```javascript
		$(document).ready(function(){
			//显示热销商品详情
			showGoodsDetails(id);
			
			//为添加购物车按钮绑定点击事件
			$(document).on("click","#btn-add-to-cart",addToCart);
		});
		
		/** 添加到购物车 */
		function addToCart(){
			$.ajax({
				"url":"/carts/add_to_cart",
				"data":{
					"gid":id,
					"num":$("#num").val()
				},
				"type":"POST",
				"dataType":"json",
				"success":function(data){
					if(data.state == 0){
						alert("添加成功！");
					}					
				},
				"error":function(){
					alert("您尚未登录，或登录超时！")
				}
			});
		}
```

### 62. 购物车-显示列表-持久层

**a. 规划SQL语句**

如果只查询`o_cart`表，查询得到的数据并不足以显示所需的页面内容，需要关联`t_goods`表进行查询，从而得到商品的标题、图片等信息，才可以完成显示：

	select 
		cid, uid, gid, title, price, image, o_cart.num
	from o_cart 
	left join t_goods
	on o_cart.gid=o_goods.id
	where uid=? 
	order by o_cart.created_time desc 

由于将执行关联查询，目前并没有哪种数据类型可以封装此次的查询结果，所以，需要使用VO类来封装查询结果！

则需要创建`com.loveoyh.store.vo.CartVO`类，该类的属性应该与查询结果保持一致：

	public class CartVO implements Serializable {
		private Integer cid;
		private Integer uid;
		private Long gid;
		private String title;
		private Long price;
		private String image;
		private Integer num;
		// SET/GET/equals/hashCode/toString
	}

**b. 接口与抽象方法**

	List<CartVO> findByUid(Integer uid);

**c. 配置映射**

映射：

	<!-- xx -->
	<!-- xx -->
	<select id="findByUid"
		resultType="com.loveoyh.store.vo.CartVO">
		xxxxx
	</select>

测试：

	@Test
	public void findByUid() {
		Integer uid = 7;
		List<CartVO> list = mapper.findByUid(uid);
		System.err.println("BEGIN:");
		for (CartVO item : list) {
			System.err.println(item);
		}
		System.err.println("END.");
	}

### 63. 购物车-显示列表-业务层

**a. 规划异常**

无

**b. 接口与抽象方法**

	List<CartVO> getByUid(Integer uid);

**c. 实现类与重写方法**

	public List<CartVO> getByUid(Integer uid) {
		return findByUid(uid);
	}
	
	privater List<CartVO> findByUid(Integer uid) {
		return cartMapper.findByUid(uid);
	}

测试：

	@Test
	public void getByUid() {
		Integer uid = 7;
		List<CartVO> list = service.getByUid(uid);
		System.err.println("BEGIN:");
		for (CartVO item : list) {
			System.err.println(item);
		}
		System.err.println("END.");
	}

### 64. 购物车-显示列表-控制器层

**a. 处理异常**

无

**b. 设计请求**

	请求路径：/carts/
	请求参数：HttpSession session
	请求类型：GET
	响应数据：JsonResult

**c. 处理请求**

	@GetMapping("/")
	public JsonResult getByUid(
			HttpSession session) {
		// 从session中获取uid
		Integer uid = getUidFromSession(session);
		// 调用业务层对象获取数据
		List<CartVO> data
			= cartService.getByUid(uid);
		// 响应
		return new JsonResult<>(SUCCESS, data);
	}

### 65. 购物车-显示列表-前端页面

核心Javascript代码：

```javascript
		<script type="text/javascript">
			$(function() {
				//返回链接
				$(".link-account").click(function() {
					location.href = "orderConfirm.html";
				})
				
				//显示购物车所有商品
				showCart();
				
				//绑定加减数量
				$("#cart-list").on("click",".add-num",increase);
				$("#cart-list").on("click",".reduce-num",reduce);
			});
			
			/** 展示用户购物车所有商品 */
			function showCart(){
				$.ajax({
					"url":"/carts/",
					"data":null,
					"type":"get",
					"dataType":"json",
					"success":function(data){
						var list = data.data;
						
						$("#cart-list").empty();
						for (var i = 0; i < list.length; i++) {
							let cart = cartTemplate.replace(/\[title\]/g,list[i].title);
							cart = cart.replace(/\[price\]/g,list[i].price);
							cart = cart.replace(/\[num\]/g,list[i].num);
							cart = cart.replace(/\[image\]/g,list[i].image);
							cart = cart.replace(/\[total\]/g,list[i].price*list[i].num);
							cart = cart.replace(/\[cid\]/g,list[i].cid);
							cart = $(cart);
							cart.data("cid",list[i].cid);
							$("#cart-list").append(cart);
						}
					}
				});
			}
			
			/** 按加号数量增 */
			function increase(e){
				...
			}
			
			/** 按减号数量减 */
			function reduce(e){
				...
			}
			
			/** 购物车模板 */
			let cartTemplate = '<tr>'
								+'<td>'
									+'<input name="cids" value="[cid]" type="checkbox" class="ckitem" />'
								+'</td>'
								+'<td><img src="..[image]collect.png" class="img-responsive" /></td>'
								+'<td>[title]</td>'
								+'<td>¥<span id="goods-price-[cid]">[price]</span></td>'
								+'<td>'
									+'<input type="button" value="-" class="num-btn reduce-num" />'
									+'<input id="inp-num-[cid]" type="text" size="2" readonly="readonly" class="num-text" value="[num]">'
									+'<input class="num-btn add-num" type="button" value="+"/>'
								+'</td>'
								+'<td>¥<span id="goods-total-[cid]">[total]</span></td>'
								+'<td>'
									+'<input type="button" class="cart-del btn btn-default btn-xs" value="删除" />'
								+'</td>'
							   +'</tr>';
		</script>
```

### 66. 购物车-增加商品数量-持久层

如果需要修改购物车中商品的数量，需要执行的SQL语句应该是：

	update o_cart set num=?, modified_user=?, modified_time=? where cid=?

以上功能已经完成，无需再次开发！

此次的操作，需要将原有的数量读取出来，并增加1，则需要查询出原有数量：

	select num from o_cart where cid=?

由于以上查询中，使用的cid是由客户端提交的参数，应该将其视为不可靠的参数！所以，在后续的执行更新之前，还需要检查数据是否存在，并检查数据归属是否正确，则在查询时，还应该查询出数据的uid，后续结合session中的uid进行对比，以保证每个用户只能修改自己的数据，所以，以上查询需要补充查询uid字段：

	select uid,num from o_cart where cid=?

以上查询尚未开发，则需要补充！

所以，需要在持久层接口中添加抽象方法：

	Cart findByCid(Integer cid);

并且，配置以上方法的映射：

	<!-- 根据购物车数据id查询购物车数据详情 -->
	<!-- Cart findByCid(Integer cid) -->
	<select id="findByCid"
		resultType="com.loveoyh.store.entity.Cart">
		SELECT
			uid, num
		FROM
			o_cart
		WHERE
			cid=#{cid}
	</select>

然后，执行单元测试：

	@Test
	public void findByCid() {
		Integer cid = 11;
		Cart result = mapper.findByCid(cid);
		System.err.println(result);
	}

### 67. 购物车-增加商品数量-业务层

此次增加数量之前，应该对即将要操作的数据进行检查，例如数据是否存在，及数据归属是否正常，则可能抛出：`CartNotFoundException`、`AccessDeniedException`，后续执行更新时还可能抛出：`UpdateException`。

则需要创建`com.loveoyh.store.service.ex.CartNotFoundException`异常类。

在业务层接口中添加抽象方法：

	Integer increase(Integer cid, Integer uid, String username) throws CartNotFoundException, AccessDeniedException, UpdateException;

以上方法返回的`Integer`表示增加后的商品数量，该数据将响应给客户端，客户端可用于显示。

在业务层实现类中，首先私有化实现持久层中新增的查询方法：

	/**
	 * 根据购物车数据id查询购物车数据详情
	 * @param cid 购物车数据id
	 * @return 匹配的购物车数据，如果没有匹配的数据，则返回null
	 */
	private Cart findByCid(Integer cid) {
		return cartMapper.findByCid(cid);
	}

然后，实现接口中的抽象方法：

	public Integer increase(Integer cid, Integer uid, String username)  throws CartNotFoundException, AccessDeniedException, UpdateException {
		// 根据参数cid查询购物车数据
		// 判断查询结果是否为null
		// 是：抛出CartNotFoundException
	
		// 判断查询结果中的uid与参数uid是否不匹配
		// 是：抛出AccessDeniedException
	
		// 从查询结果中取出num，增加1，得到新的数量
		// 更新商品数量
	}

具体实现：

	@Override
	public Integer increase(Integer cid, Integer uid, String username)
			throws CartNotFoundException, AccessDeniedException, UpdateException {
		// 根据参数cid查询购物车数据
		Cart result = findByCid(cid);
		// 判断查询结果是否为null
		if (result == null) {
			// 是：抛出CartNotFoundException
			throw new CartNotFoundException(
				"增加数量失败！尝试访问的购物车数据不存在！");
		}
	
		// 判断查询结果中的uid与参数uid是否不匹配
		if (result.getUid() != uid) {
			// 是：抛出AccessDeniedException
			throw new AccessDeniedException(
				"增加数量失败！非法访问！");
		}
	
		// 从查询结果中取出num，增加1，得到新的数量
		Integer newNum = result.getNum() + 1;
		// 更新商品数量
		updateNum(cid, newNum, username, new Date());
		// 返回
		return newNum;
	}

最后，编写并执行单元测试：

	@Test
	public void increase() {
		try {
			Integer cid = 10;
			Integer uid = 7;
			String username = "系统管理员";
			Integer newNum = service.add(cid, uid, username);
			System.err.println("OK. new num=" + newNum);
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

### 68. 购物车-增加商品数量-控制器层

首先，需要在`BaseController`中添加对`CartNotFoundException`的处理！

然后，在`CartController`中添加处理请求的方法：

	@RequestMapping("{cid}/add")
	public JsonResult add(
		@PathVariable("cid") Integer cid,
		HttpSession session) {
		// 从session中获取uid和username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		// 执行
		Integer data = cartService.increase(cid, uid, username);
		// 响应
		return new JsonResult<>(SUCCESS, data);
	}

完成后，打开浏览器，先登录，然后通过`http://localhost:8080/carts/9/add`进行测试。

```java
注：
    减法类似加法处理即可
```

### 69. 购物车-增加商品数量-前端界面

核心javascript代码：

```javascript
			/** 按加号数量增 */
			function increase(e){
				var event = e || window.event;
				var target = event.target || event.srcElement;
				let cart = $(target).parent().parent();
				let cid = cart.data("cid");
				$.ajax({
					"url":"/carts/"+cid+"/increase",
					"data":null,
					"type":"POST",
					"dataType":"json",
					"success":function(data){
						if(data.state == 0){
							let num = data.data;
							$("#inp-num-"+cid).val(parseInt(num));
							$("#goods-total-"+cid).html(parseFloat($("#goods-price-"+cid).html())*num);
						}
					},
					"error":function(){
						alert("login timeout!")
					}
				});
			}
			
			/** 按减号数量减 */
			function reduce(e){
				var event = e || window.event;
				var target = event.target || event.srcElement;
				let cart = $(target).parent().parent();
				let cid = cart.data("cid");
				$.ajax({
					"url":"/carts/"+cid+"/reduce",
					"data":null,
					"type":"POST",
					"dataType":"json",
					"success":function(data){
						if(data.state == 0){
							let num = data.data;
							$("#inp-num-"+cid).val(parseInt(num));
							$("#goods-total-"+cid).html(parseFloat($("#goods-price-"+cid).html())*num);
						}
					},
					"error":function(){
						alert("login timeout!");
					}
				});
			}
```

### 70. 显示确认订单页-收货地址列表

在“确认订单”页面中，需要显示2种数据：当前登录的用户的收货地址列表，用户在前序页面中勾选的购物车数据列表。

如果需要显示收货地址列表，基本上无需开发服务器端的功能！因为此前已经完成！登录后，向`http://localhost:8080/addresses/`发出请求，即可获取当前登录的用户的收货地址列表。

所以，此次只需要在`orderConfirm.html`页面中，当页面加载完成时，依然向`http://localhost:8080/addresses/`发出请求，并将获取的数据显示到下拉列表中即可！

### 71. 显示确认订单页-勾选的购物车数据列表-持久层

此次需要显示的购物车数据列表是根据用户在前序页面中的勾选来决定的，则对应的SQL查询语句大致是：

	select 
		cid, uid, gid, title, price, image, o_cart.num
	from o_cart 
	left join o_goods
	on o_cart.gid=o_goods.id
	where cid in (??, ??, ??)
	order by o_cart.created_time desc

则应该在`CartMapper`接口中添加抽象方法：

	List<CartVO> findByCids(Integer[] cids);

然后，在`CartMapper.xml`中配置以上抽象方法的映射：

	<select id="findByCids" resultType="xx.xx.xx.xx.CartVO">
		select 
			cid, uid, gid, title, price, image, o_cart.num
		from o_cart 
		left join o_goods
		on o_cart.gid=o_goods.id
		where cid in (
			<foreach collection="array" item="cid" separator=",">
				#{cid}
			</foreach>
		)
		order by o_cart.created_time desc
	</select>

最后，编写并执行单元测试：

### 72. 显示确认订单页-勾选的购物车数据列表-业务层

此次并不会抛出任何异常。

复制持久层中的抽象方法，将其粘贴到业务层接口中，并将`find`改成`get`：

	List<CartVO> getByCids(Integer[] cids, Integer uid);

打业务层实现类中，先私有化实现持久层的同名方法：

	/**
	 * 根据数据id获取购物车数据列表
	 * @param cids 数据id
	 * @return 匹配的购物车数据列表
	 */
	private List<CartVO> findByCids(Integer[] cids) {
		return cartMapper.findByCids(cids);
	}

然后，重新接口中的抽象方法：

	public List<CartVO> getByCids(Integer[] cids, Integer uid) {
		// 查询数据
		List<CartVO> results = findByCids(cids);
		// 逐一判断结果中的每一条数据，是否都是当前用户的数据
		// 如果不是当前用户的数据，移除该数据
		Iterator<CartVO> it = result.iterator();
		while (it.hasNext()) {
			if (uid != it.next().getUid()) {
				it.remove();
			}
		}
		// 返回
		return results;
	}

完成后，编写并执行单元测试：

	@Test
	public void getByCids() {
		Integer[] cids = {12,9,13,14};
		Integer uid = 7;
		List<CartVO> list = service.getByCids(cids, uid);
		System.err.println("BEGIN:");
		for (CartVO item : list) {
			System.err.println(item);
		}
		System.err.println("END.");
	}

### 73. 显示确认订单页-勾选的购物车数据列表-控制器层

此次不需要处理异常。

直接在`CartController`中添加处理请求的方法：

	@GetMapping("get_by_cids")
	public JsonResult getByCids(
		Integer[] cids, HttpSession session) {
		// 从session中获取uid
		Integer uid = getUidFromSession(session);
		// 执行
		List<CartVO> data = cartService.getByCids(cids, uid);
		// 响应
		return new JsonResult<>(SUCCESS, data);
	}

完成后，可以通过`http://localhost:8080/carts/get_by_cids?cids=9&cids=10&cids=13&cids=14`进行测试。

### 74. 创建订单-创建数据表

订单相关数据必须使用2张表才可以存储，所以，需要先创建订单表：

	CREATE TABLE o_order (
		oid INT AUTO_INCREMENT COMMENT '订单id',
		uid INT COMMENT '用户id',
		recv_name VARCHAR(50) COMMENT '收货人姓名',
		recv_phone VARCHAR(20) COMMENT '收货人电话',
		recv_address VARCHAR(250) COMMENT '收货人地址',
		total_price BIGINT(20) COMMENT '总价',
		state INT COMMENT '状态：0-未支付，1-已支付，2-已取消',
		order_time DATETIME COMMENT '下单时间',
		pay_time DATETIME COMMENT '支付时间',
		created_user VARCHAR(20) COMMENT '创建人',
		created_time DATETIME COMMENT '创建时间',
		modified_user VARCHAR(20) COMMENT '最后修改人',
		modified_time DATETIME COMMENT '最后修改时间',
		PRIMARY KEY (oid)
	) DEFAULT CHARSET=UTF8;

然后，还要创建订单商品表：

	CREATE TABLE o_order_item (
		id INT AUTO_INCREMENT COMMENT 'id',
		oid INT COMMENT '订单id',
		gid BIGINT COMMENT '商品id',
		title VARCHAR(100) COMMENT '商品标题',
		image VARCHAR(500) COMMENT '商品图片',
		price BIGINT COMMENT '商品单价',
		num INT COMMENT '购买数量',
		created_user VARCHAR(20) COMMENT '创建人',
		created_time DATETIME COMMENT '创建时间',
		modified_user VARCHAR(20) COMMENT '最后修改人',
		modified_time DATETIME COMMENT '最后修改时间',
		PRIMARY KEY (id)
	) DEFAULT CHARSET=UTF8;

### 75. 创建订单-创建实体类

创建`com.loveoyh.store.entity.Order`实体类：

	public class Order extends BaseEntity {
	
		private static final long serialVersionUID = -7634515703569972463L;
	
		private Integer oid;
		private Integer uid;
		private String recvName;
		private String recvPhone;
		private String recvAddress;
		private Long totalPrice;
		private Integer state;
		private Date orderTime;
		private Date payTime;
		
		...
	}

创建`com.loveoyh.store.entity.OrderItem`实体类：

	public class OrderItem extends BaseEntity {
	
		private static final long serialVersionUID = 58159245395814558L;
	
		private Integer id;
		private Integer oid;
		private Long gid;
		private String title;
		private String image;
		private Long price;
		private Integer num;
		
		...
	}

### 76. 创建订单-持久层

创建`com.loveoyh.store.mapper.OrderMapper`接口，并添加抽象方法：

	Integer insertOrder(Order order);
	
	Integer insertOrderItem(OrderItem orderItem);

复制得到`OrderMapper.xml`，修改根节点的`namespace`属性对应以上接口，并配置以上2个抽象方法的映射：

	<mapper namespace="com.loveoyh.store.mapper.OrderMapper">
		
		<!-- 插入订单数据 -->
		<!-- Integer insertOrder(Order order) -->
		<insert id="insertOrder"
			useGeneratedKeys="true"
			keyProperty="oid">
			INSERT INTO t_order (
				uid, recv_name,
				recv_phone, recv_address,
				total_price, state,
				order_time, pay_time,
				created_user, created_time,
				modified_user, modified_time
			) VALUES (
				#{uid}, #{recvName},
				#{recvPhone}, #{recvAddress},
				#{totalPrice}, #{state},
				#{orderTime}, #{payTime},
				#{createdUser}, #{createdTime},
				#{modifiedUser}, #{modifiedTime}
			)
		</insert>
		
		<!-- 插入订单商品数据 -->
		<!-- Integer insertOrderItem(OrderItem orderItem) -->
		<insert id="insertOrderItem"
			useGeneratedKeys="true"
			keyProperty="id">
			INSERT INTO o_order_item (
				oid, gid,
				title, image,
				price, num,
				created_user, created_time,
				modified_user, modified_time
			) VALUES (
				#{oid}, #{gid},
				#{title}, #{image},
				#{price}, #{num},
				#{createdUser}, #{createdTime},
				#{modifiedUser}, #{modifiedTime}
			)
		</insert>
		
	</mapper>

在**src/test/java**下创建`com.loveoyh.store.mapper.OrderMapperTests`测试类，编写并执行测试方法：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class OrderMapperTests {
	
		@Autowired
		private OrderMapper mapper;
		
		@Test
		public void insertOrder() {
			Order order = new Order();
			order.setUid(1);
			Integer rows = mapper.insertOrder(order);
			System.err.println("rows=" + rows);
		}
		
		@Test
		public void insertOrderItem() {
			OrderItem orderItem = new OrderItem();
			orderItem.setOid(1);
			Integer rows = mapper.insertOrderItem(orderItem);
			System.err.println("rows=" + rows);
		}
		
	}

### 77. 创建订单-业务层

此次涉及的都是新增数据操作，新增之前几乎没有需要验证的规则，可能抛出的异常主要还是新增数据时的`InsertException`。

在处理业务过程中，需要“根据收货地址aid查询收货地址详情”，目前，在`AddressMapper`中已经定义了`findByAid()`，但是查询的字段列表并不符合要求，需要先补充查询所需的字段：

	<!-- 根据收货地址的数据id查询详情 -->
	<!-- Address findByAid(Integer aid) -->
	<select id="findByAid"
		resultType="com.loveoyh.store.entity.Address">
		SELECT
			uid, is_default AS isDefault,
			name, phone,
			province_name AS provinceName,
			city_name AS cityName,
			area_name AS areaName,
			address
		FROM 
			o_address
		WHERE 
			aid=#{aid}
	</select>

然后，还需要在`AddressService`中添加`getByAid()`方法：

	/**
	 * 根据收货地址的数据id查询详情
	 * @param aid 收货地址的数据id
	 * @return 匹配的收货地址的详情，如果没有匹配的数据，则返回null
	 */
	Address getByAid(Integer aid);

并在`AddressServiceImpl`中实现以上方法，由于此前已经私有化实现了`findByAid()`方法，所以，此次实现时直接调用即可：

	public Address getByAid(Integer aid) {
		return findByAid(aid);
	}

先创建`com.loveoyh.store.service.OrderService`业务层接口，并添加抽象方法：

	Order create(Integer aid, Integer[] cids, Integer uid, String username) throws InsertException;

然后创建`com.loveoyh.store.service.OrderServiceImpl`业务层实现类，实现以上接口，添加`@Service`注解，在类中声明`@Autowired private OrderMapper orderMapper;`持久层对象，并声明`@Autowired private AddressService addressService;`和`@Autowired private CartService cartService;`：

	@Service
	public class OrderServiceImpl implements IOrderService {
	
		@Autowired private OrderMapper orderMapper;
		@Autowired private AddressService addressService;
		@Autowired private CartService cartService;
	
	}

先私有化实现持久层中的方法：

	/**
	 * 插入订单数据
	 * @param order 订单数据
	 * @throws InsertException 插入数据异常
	 */
	private void insertOrder(Order order) throws InsertException {
		Integer rows = orderMapper.insertOrder(order);
		if (rows != 1) {
			throw new InsertException(
				"创建订单失败！插入订单数据时出现未知错误！");
		}
	}
	
	/**
	 * 插入订单商品数据
	 * @param orderItem 订单商品数据
	 * @throws InsertException 插入数据异常
	 */
	private void insertOrderItem(OrderItem orderItem) throws InsertException {
		Integer rows = orderMapper.insertOrderItem(orderItem);
		if (rows != 1) {
			throw new InsertException(
				"创建订单失败！插入订单商品数据时出现未知错误！");
		}
	}

然后再重写接口中定义的抽象方法：

	@Transactional
	public Order create(Integer aid, Integer[] cids, Integer uid, String username) throws InsertException {
		// 创建当前时间对象：now
	
		// 根据参数cids查询对应的购物车数据，得到List<CartVO>对象
		// 遍历以上查询到的对象，根据各元素的price和num计算得到总价
	
		// 根据收货地址aid查询收货地址详情
	
		// 创建order对象：Order order = new Order();
		// 封装order对象中的属性值：order.setUid(uid);
		// 封装order对象中的属性值：recv_name,recv_phone,recv_address
		// 封装order对象中的属性值：total_price
		// 封装order对象中的属性值：state(0)
		// 封装order对象中的属性值：order_time(now)
		// 封装order对象中的属性值：pay_time(null)
		// 封装order对象中的属性值：日志
		// 插入订单数据：insertOrder(order)
	
		// 遍历以上查询得到的List<CartVO>对象
		// -- 创建orderItem对象：OrderItem ordeItemr = new OrderItem();
		// -- 封装orderItem对象中的属性值：oid
		// -- 封装orderItem对象中的属性值：gid,price,title,image,num
		// -- 封装orderItem对象中的属性值：日志
		// -- 插入订单商品数据：insertOrderItem(itemItem)
	}

具体实现为：

	@Override
	@Transactional
	public Order create(Integer aid, 
		Integer[] cids, Integer uid, String username) throws InsertException {
		// 创建当前时间对象：now
		Date now = new Date();
	
		// 根据参数cids查询对应的购物车数据，得到List<CartVO>对象
		List<CartVO> carts = cartService.getByCids(cids, uid);
		// 遍历以上查询到的对象，根据各元素的price和num计算得到总价
		Long totalPrice = 0L;
		for (CartVO cartVO : carts) {
			totalPrice += cartVO.getPrice() * cartVO.getNum();
		}
	
		// 根据收货地址aid查询收货地址详情
		Address address = addressService.getByAid(aid);
	
		// 创建order对象：Order order = new Order();
		Order order = new Order();
		// 封装order对象中的属性值：order.setUid(uid);
		order.setUid(uid);
		// 封装order对象中的属性值：recv_name,recv_phone,recv_address
		order.setRecvName(address.getName());
		order.setRecvPhone(address.getPhone());
		order.setRecvAddress(address.getProvinceName() + address.getCityName() + address.getAreaName() + address.getAddress());
		// 封装order对象中的属性值：total_price
		order.setTotalPrice(totalPrice);
		// 封装order对象中的属性值：state(0)
		order.setState(0);
		// 封装order对象中的属性值：order_time(now)
		order.setOrderTime(now);
		// 封装order对象中的属性值：pay_time(null)
		order.setPayTime(null);
		// 封装order对象中的属性值：日志
		order.setCreatedUser(username);
		order.setCreatedTime(now);
		order.setModifiedUser(username);
		order.setModifiedTime(now);
		// 插入订单数据：insertOrder(order)
		insertOrder(order);
	
		// 遍历以上查询得到的List<CartVO>对象
		for (CartVO cartVO : carts) {
			// 创建orderItem对象：OrderItem ordeItemr = new OrderItem();
			OrderItem orderItem = new OrderItem();
			// 封装orderItem对象中的属性值：oid
			orderItem.setOid(order.getOid());
			// 封装orderItem对象中的属性值：gid,price,title,image,num
			orderItem.setGid(cartVO.getGid());
			orderItem.setPrice(cartVO.getPrice());
			orderItem.setTitle(cartVO.getTitle());
			orderItem.setImage(cartVO.getImage());
			orderItem.setNum(cartVO.getNum());
			// 封装orderItem对象中的属性值：日志
			orderItem.setCreatedUser(username);
			orderItem.setCreatedTime(now);
			orderItem.setModifiedUser(username);
			orderItem.setModifiedTime(now);
			// 插入订单商品数据：insertOrderItem(itemItem)
			insertOrderItem(orderItem);
		}
		
		// TODO 删除购物车中对应的数据
		// TODO 修改对应的商品的库存量
	
		// 返回
		return order;
	}

最后，编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class OrderServiceTests {
	
		@Autowired
		private OrderService service;
		
		@Test
		public void create() {
			Integer aid = 20;
			Integer[] cids = {7,9,12,13};
			Integer uid = 7;
			String username = "超级管理员";
			Order order = service.create(aid, cids, uid, username);
			System.err.println(order);
		}
		
	}

### 78. 创建订单-控制器

创建`com.loveoyh.store.controller.OrderController`控制器类，继承自`BaseController`，添加`@RestController`和`@RequestMapping("orders")`注解，在类中声明`@Autowired private OrderService orderService;`业务层对象：

	@RestController
	@RequestMapping("orders")
	public class OrderController extends BaseController {
	
		@Autowired 
		private OrderService orderService;
	
	}

然后，在类中添加处理请求的方法：

	@RequestMapping("create")
	public JsonResult create(
		Integer aid, Integer[] cids, HttpSession session) {
		// 从Session中获取uid和username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		// 调用业务层对象执行
		Order data = orderService.create(aid, cids, uid, username);
		// 响应
		return new JsonResult<>(SUCCESS, data);
	}

完成后，可以先登录，然后通过`http://localhost:8080/orders/create?aid=20&cids=9&cids=10&cids=12&cids=13`进行测试。











### ------------------------------------

### 附1：关于VARCHAR的设置

在MySQL数据库中，如果某字段的类型设置为`VARCHAR(30)`，表示访字段最多存储30个字节长度的数据，如果尝试存入的数据超出30字节，多余的部分将不会被存储！

30个字节，表示为多少个字符，取决于使用的字符编码！

以UTF8为例，如果存储的内容是1个字节的，存储下来例如：

	0110 0001


其规则是使用第1位0表示该字符只占1个字节，剩余的7位表示1个字符！

如果某个字符需要使用2个字节才可以表示，UTF8的规则表现例如：

	110 01010	10 000111


即第1个字节使用110作为前缀，表示该字符需要2个字节，其中，这里的2个1就表示共要2个字节，后续的第2个字节中的10就表示它是跟随前序字节一起的，第2个字节中的剩余6位才是编码位。

在2个字节中，只有5+6=11个编码位，只能表示2048种不同的组合，所以，在UTF8编码中，2个字节是不可以表示中文的，因为中文里的汉字数量及相关符号远超2048个！所以，中文需要3个字节来表示，在UTF8中，3字节的编码例如：

	1110 0101	10 101010	10 010101


在UTF8中，还有需要4字节才可以表示的编码……

所以，如果设计的是`VARCHAR(30)`，并使用UTF8编码，到底可以存入多少字符，也是不确定的，如果存的是ASCII字符，最多可以存30个，如果存的是汉字，只能计划为10个，如果是混合的，可以计划为例如15个英文加上5个汉字……所以，通常，在设置时，只能根据最大需求来进行设计！

另外，请还注意一个问题：设置的字节值，并不代码存储占用空间！例如将`VARCHAR(30)`存满，还另外会占用1个字节存储长度，表示存了多少字节的数据，在存储长度时，数据类型的字节数在255个时是分隔线，如果使用`VARCHAR`设置的字节数超过255，则另外需要占用2个字节存储长度！例如设置`VARCHAR(300)`，则额外的存储长度时使用字节数就是2个字节！

### 附2：密码加密

加密算法并不适合对密码进行加密！因为，所以的加密算法都是可以逆运算的，如果使用的算法和加密时使用的参数是已知的，则可以轻松推算出原始数据！而密码安全问题主要源自内部泄密！也就是说，数据库的数据能泄密，可以认为算法和加密参数也是有泄密的可能的！

对于密码安全问题而言，最好的做法就是将密码进行加密，却任何人都无法解密！则，在密码加密这个问题上，所有的加密算法都是不适用的！只能使用摘要算法！

摘要算法的全称是“消息摘要算法”，这种算法的特征有：

1. 消息原文相同时，得到的摘要是相同的；

2. 使用的摘要算法没有发生变化时，无论原文长度是多少，摘要的长度是固定的；

3. 消息原文不同时，得到的摘要几乎不会相同！

在消息摘要领域中，一定存在N种不同的原文，可以计算得到完全相同的摘要！

使用MD5算法执行摘要运算时，得到的结果都是32位长度的十六进制数，转换成二进制就需要128位，所以，MD5也被称之为128位的算法！

如果有2个不同的原文，可以得到相同的摘要，这种状况称之为“碰撞”，在有限长度的原文中，得到相同的摘要的概率是极低，甚至就是不可能的！

所以，真正用于对密码加密的算法，都是摘要算法，主要原因是因为它不可逆，且在有限长度的原文的基础之上，几乎不可能发生碰撞！

常见的摘要算法有MD5家族和SHA系列，例如：MD2、MD4、MD5、SHA1、SHA128、SHA256、SHA384、SHA512……其中，MD家族的都是128的，SHA系列名称后有位数的，就是对应的位数。

关于MD5或相关摘要算法的破解研究是存在的，主要是针对算法的碰撞攻击，而并不是尝试执行逆运算来得到原始数据！

另外，还有许多网站号称可以在线破解MD5，只需要将摘要结果填进去，就可以查到原始数据，例如填入`e10adc3949ba59abbe56e057f20f883e`就可以查出`123456`，事实上，这些网站是记录了大量的原始数据与MD5摘要数据的对应关系，如果原始数据比较简单，或者是常用密码值，被这些网站收录的可能性就非常大！所以，为了提升密码的安全性，保证密码不被这些网站“破解”，可以采取的做法：

1. 增强原始密码的安全强度，例如从组成元素、长度方面提出更高要求；
2. 反复执行加密，即多重加密；
3. 加盐；
4. 综合以上所有方式。




	select 原始数据 from 表 where 摘要数据=?
	
	10 + 52 + ? > 80
	
	1 > 80
	2 > 6400
	3 > 480000
	4 > 40000000
	5 > 3200000000
	
	8 > 
	
	123456
	e10adc3949ba59abbe56e057f20f883e
	
	1
	c4ca4238a0b923820dcc509a6f75849b
	
	111111111111111111111111111111111111111111
	636fa4bad5bf92137374e947b3c424f0

### 附3：设置SpringBoot项目上传限制

在启动类之前添加`@Configuration`注解，并在类中添加：

	@Bean
	public MultipartConfigElement getMultipartConfig() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		
		DataSize maxFileSize = DataSize.ofMegabytes(50);
		factory.setMaxFileSize(maxFileSize);
		DataSize maxRequestSize = DataSize.ofMegabytes(100);
		factory.setMaxRequestSize(maxRequestSize);
		
		return factory.createMultipartConfig();
	}

### 附4：SpringBoot项目RestController和Controller

@RestController注解相当于@ResponseBody ＋ @Controller合在一起的作用。 

1.  如果只是使用@RestController注解Controller，则Controller中的方法无法返回jsp页面，或者html，配置的视图解析器 InternalResourceViewResolver不起作用，返回的内容就是Return 里的内容。

2. 如果需要返回到指定页面，则需要用 @Controller配合视图解析器InternalResourceViewResolver才行。如果需要返回JSON，XML或自定义mediaType内容到页面，则需要在对应的方法上加上@ResponseBody注解。

### 附5：基于Spring-JDBC的事务(Transaction)

首先，事务是用于保障数据安全的，可以使得一系列的增删改操作，要么全部成功，要么全部失败！

**什么情况下需要使用事务：**当某个业务涉及2次或更多次的增、删、改操作时，必须使用事务！例如执行2条Update语句，或1条Insert语句和1条Update语句等。

**如何使用事务：**基于Spring-JDBC的编程中，只需要在业务方法之前添加`@Transactional`注解即可。

框架在处理事务时，大致的执行过程是：

	开启事务：begin
	try {
		执行各任务
		提交：commit
	} catch (RuntimeException e) {
		回滚：rollback
	}

所以，为了保证`@Transactional`注解能够正常保障事务，在每次的增、删、改操作执行完成后，都必须判断受影响的行数是否是预期值，如果不是预期值，必须抛出`RuntimeException`或其某个子孙类的异常！

另外，`@Transactional`注解还可以添加在业务类之前，表示该类中所有的方法在执行时，都是有事务保障的！通常并不推荐这样处理！

### 附6：数据的验证原则

通常，客户端应该完成数据的基本验证，然后才会将数据提交到服务器，所谓的基本验证通常指的是数据格式的验证，例如字符串的长度、字符串的组成、固定的格式等等。

当客户端完成了这些验证后，基本格式错误的数据将不会被提交的服务器！但是，并不是绝对的，当服务器接收到来自客户端的数据时，第一时间就应该再次验证这些数据！因为客户端是在用户自己手中的，是有可能被恶意篡改的，这一点服务器是无法把控的，所以，服务器并不能完全信任来自客户端的数据！

虽然服务器端会完成数据的验证，也并不代表客户端不需要验证，毕竟能够绕过客户端验证并提交的数据是少部分的，客户端的验证机制能拦截绝大部分数据有误的请求，从而减轻服务器压力！

甚至，在一些特殊的应用中，服务器端的`Controller`进行了数据验证，但是，`Service`还会再次验证！多半源自于这些应用中，`Service`并不一定是由`Controller`调用的，例如某些计划任务等。

所以，关于数据验证，需要验证的节点有：客户端、服务器端的`Controller`、服务器端的`Service`，各自解决不同的问题。

### 附7：Eclipse常用快捷键

	代码提示：Alt + /
	添加或移除单行注释：Ctrl + /
	换行：Shift + Enter
	解决方案：Ctrl + 1
	格式化代码：Ctrl + Shift + F
	整理导包语句：Ctrl + Shift + O
	代码整行移动：Alt + 上/下
	复制整行代码：Ctrl + Alt + 上/下
	删除整行代码：Ctrl + D
	定位光标到行首/行尾：Home/End
	定位光标到文首/文尾：Ctrl + Home/End
	文档内重命名：选中后，Ctrl + 2, R

