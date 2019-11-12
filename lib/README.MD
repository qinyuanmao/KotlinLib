# Android Kotlin基础框架 （待完善）

---

**代码目录**
- network
    - http 
        - EncodeExtend 加密解密扩展函数
        - HeaderFactory 参数请求头构造器
        - Method 请求方式枚举类
        - NetworkAction 请求的Action
        - ParamsFactory 请求的参数构造器
        - RequireFactory 请求的配置项构造器
        - HttpFactory 网络请求工厂
        - Response 请求返回结果
        - UploadRequestBody 上传文件请求的body
    - socket
        - Socket socket连接工厂
        - SocketManager socket管理工具
        - SocketParam Socket参数配置
        - SocketRunningTime Socket运行返回
    - NetworkFactory 网络请求总配置项
    - UrlManager.kt 设置测试环境地址和正式环境地址，DEBUG返回测试地址，RELEASE返回正式环境地址。
- ui
    - ActivityExtend.kt Activity扩展函数
    - BaseActivity.kt 
    - BaseFragment.kt
    - BaseRecyclerAdapter.kt 封装RecyclerAdapter
    - FragmentAdapter.kt ViewPager必备
    - FragmentExtend.kt Fragment扩展函数
    - SimpleRecyclerAdapter.kt 封装常用的RecycleAdapter
- update
    - UpdateActivityExtend app更新扩展方法
    - FileBean 文件bean
    - UpdateBean app更新的VersionBean
    - UpdateRequest app更新的参数构造器
- db
    - annotation 注解目录
        - AnnotationExpression 解析注解
        - COL 数据库列，传入type，如TEXT，VARCHAR，INTEGER等
        - FK 数据库外键，传入type、外键对应的表、外键对应的col
        - PK 数据库主键，传入type
        - TABLE 数据库表名，传入数据库表名
    - CursorExtend Cursor转化为model和List的扩展函数
    - DBModel 所有数据库model的超类
    - DemoModel 数据库model的示例
    - ModelExtend 数据库model的插入、更新、删除扩展函数
    - Select 数据库查询操作
    - WisesoftDBHelper 数据库操作基类
- crash 崩溃日志记录
    - CrashManager 崩溃日志主要文件
- provider
    - FileProvider
- service 封装通用的Service对象
    - ConnectionAction 连接时操作对象
    - NormalBinder Service 绑定
    - NormalService 通用Service对象
- utils
    - ActivityContainer Activity栈
    - BundleExtend bundle扩展函数
    - CacheManager 数据缓存（SnappyDB框架）
    - ContextException Context调用错误Exception
    - HashMapExtend HashMap扩展函数
    - FileExtend 文件扩展函数
    - ImageManager Image图片加载和图片三级缓存获取
    - InputStreamExtend InputStream扩展函数
    - LocalCacheManager 本地缓存
    - MemoryCacheManager 内存缓存
    - ObserverManager 观察者模式
    - SerializableExtend Serializable扩展函数
    - StringExtend String扩展函数
    - TaskManager 协程异步任务
    - ToastUtil Toast工具
- view 自定义空间库
    - CommItemDecoration 处理RecyclerView分割线
    - EditTextExtend EditText扩展函数
    - ImageViewExtend ImageView的扩展函数
    - RecyclerViewExtend RecyclerView扩展函数
    - RedPointView 小红点角标
    - WebViewExtend WebView扩展函数
- KotlinLib.kt Library初始化
    
# 代码使用

## 简述

所有以Extend结尾的文件均为扩展函数，调用的时候直接通过 className.method()，如imageView.loadUrl(url)。

## 初始化

在Application中调用KotlinLib.init(context)

---

## network

### NetworkFactory
> 配置网络请求工厂

```kotlin
initNetworkFactory {
    client = OkHttpClient() //默认的OKHttpClient
    header = mapOf( //默认的header
        "token" to "123456",
        "userId" to "123456"
    )
    body = mapOf( //默认的参数
        "token" to "123456",
        "userId" to "123456"
    )
    mediaType = MediaType.parse("application/octet-stream") //请求头
    aesPassword = "wisesoft" //AES加密啊key
    paramKey = "param" //请求参数包裹key
}
```

### http

#### EncodeExtend
> String AES加密解密，默认NetworkFactory的paramKey

```kotlin
val value = "123".aesEncode("wisesoft")
val value = "123".aesDecode("wisesoft")
```

#### HeaderFactory
> Header构造器，结合RequireFactory使用

#### HttpFactory
> http请求核心代码，后面代码demo中详细介绍

#### Method
> 请求type枚举类

#### NetworkAction
> 请求体封装器，主要需要编写的代码

#### ParamsFactory
> 参数构造器

#### RequireFactory
> 请求体构造器，主要编写的代码，对接ParamsFactory， HeaderFactory

#### Response
> 封装的返回结果，区别于okhttp的Response

#### UploadRequestBody
> 文件上传返回进度的RequestBody

#### 网络请求使用
> 结合后面的TaskManager使用，TaskManager中加了UI的返回结果在UI线程，否则在子线程，如networkUiTask在主线程，networkTask在子线程。所有的UiTask都会添加到BaseActivity的任务列，在异步返回时无需处理activity是否被finish。

```kotlin
//http请求
networkTask<User> {
    param = createRequire {
        reqEncode = true //请求体是否加密，默认false
        respEncode = true //返回体是否加密，默认false
        url = "https://app.wisesoft.net.cn/api/v1/user/login" //请求地址，设置了UrlManager会判断是否有默认请求地址，并拼接
        headers = {
            // 请求头,key value都是String
            "token" .. "123"
            "userId" .. "123"
        }
        params = {
            //请求体
            "username" .. "admin"
            "password" .. "admin@123"
        }
        method = Method.POST //默认POST
    }
    success = { // it is User
        println(it)
        // TODO 执行登录操作
    }
    falied = { // it is String，可选，默认是println()，ui线程默认toast
        println(it)
    }
}

//文件上传
uploadUiTask<FileBean> {
    param = createUpload {
        channel = Channel<String?>() //协程获取进度通道，不熟悉的情况下不建议自定义
        context = this@MainActivity //context
        url = "http://172.16.122.247:7778/file/uploadFile" // 请求地址
        headers { // 请求头
            "userId"..userId
            "userType"..Mobileim.LoginFromAndroid
        }
        fileKey = "upload" // 文件key
        filePath = path //文件路径
        reqEncode = true //请求体是否加密，默认false
        respEncode = true //返回体是否加密，默认false
        method = Method.POST //默认POST
        params { //请求参数
            "md5"..path.getFileMD5()
        }
    }
    onProcess = { process -> // it is String
        dialog.setMessage("正在上传头像： $process%")
    }
    success = { node -> //it is FileBean
        // TODO 执行文件上传完操作
    }
    falied = { // it is String，可选，默认是println()，ui线程默认toast
        println(it)
    }
}

//文件下载
downloadUiTask {
    param = createDownload {
        channel = Channel<String?>() //协程获取进度通道，不熟悉的情况下不建议自定义
        context = this@MainActivity //context
        url = "http://172.16.122.247:7778/file/uploadFile" // 请求地址
        headers { // 请求头
            "userId"..userId
            "userType"..Mobileim.LoginFromAndroid
        }
        savePath = path //文件保存路径，默认/sdk_card0/packageName/files
        fileName = "xxx.png" //文件保存名，默认服务器返回的名字
        reqEncode = true //请求体是否加密，默认false
        method = Method.POST //默认POST
        params { //请求参数
            "md5"..path.getFileMD5()
        }
    }
    onProcess = { dialog.setMessage("正在下载：$it%") }
    success = { // it is String，it = savePath
        // TODO 下载完毕之后
    }
}
```

### socket
> 长连接，目前只支持websocket

#### SocketAction
> Socket连接核心代码

#### SocketManager
> 多个Socket时的管理器，所有的Socket请求都会通过SocketManager进行管理，可以通过key进行消息发送，socket关闭等。

```kotlin
SocketManager.INSTANCE.addSocket("socket1", createSocketParam {..})
SocketManager.INSTANCE.removeSocket("socket1")
SocketManager.INSTANCE.removeAll()
SocketManager.INSTANCE.sendMessage("hello socket server!", "socket1")
```

#### SocketParam
> Socket 参数管理器

#### SocketRunningTime
> 连接SocketParam，Socket主要编码部分

#### Socket使用示例
> 通过connectSocket创建的socket对象自动添加到SocketManager中。

```kotlin
connectSocket {
    param = createSocketParam { //参数
        url = "ws://qinyuanmao.cn:8081" //socket连接地址
        heartBlock = { // 心跳上传内容，返回的就是心跳内容
            return "heart"
        }
        context = ctx //context
        reqEncode = true //请求体是否加密，默认false
        respEncode = true //返回体是否加密，默认false
        headers { // 请求头
            "userId"..userId
            "userType"..Mobileim.LoginFromAndroid
        }
        heartTime = 5 * Time.SECOND //心跳时间，默认5s
        key = "socket1" //socket管理器关键字，默认default
        needReconnect = true //需要断线重连，默认true
        reconnectTime = 5 * Time.SECOND //断线重连时间，默认5s
    }
    onOpen = { // it is response
        // 连接开始时
    }
    onMessage = { // it is response
        // 收到消息时
    }
    onClosing = { // it is response
        // TODO 连接正在关闭
    }
    onClosed = { // it is response
        // TODO 连接被关闭
    }
    onFailure = { // it is response
        // TODO 连接失败
    }
}
```

### UrlManager.kt

Application中初始化

```kotlin
Api.initApi(debugUrl, releaseUrl)
```

调用

```kotlin
val url = Api.getUrl() //自动判断环境
```

---

## service
> 通用Service操作，可以在Activity中直接进行Service操作

### ConnectionAction
> Connection事件接口

### NormalBinder
> Service的Binder

### NormalService
> 封装的通用Service对象，已注册到Manifest中

### 使用示例

```kotlin
service { // activity和fragment中有此方法
    connected = { // 连接时干啥
        var count = 1
        delayTask(2000) {
            println("Service 1:${++count}")
        }
    }
    disconnected = { //断开时干啥
        println("Service 1: Finish")
    }
}
```

---

## ui

### ActivityExtend

在Activity的onCreate()方法中初始化toolbar

直接调用 initToolbar(mToolbar, title, backable/* *参数可选，默认是有返回箭头和事件* */)

### BaseActivity

BaseActivity只提供了加入注销Activity栈功能和mContext，其他部分功能已经分解到Activity和Context扩展函数中，可根据自己习惯自定义；

需要全屏，在onCreate()的super方法之前调用

```kotlin
setNeedFull(true)
```

### BasePermissionActivity

> 带权限管理的BaseActivity，需要的地方继承

```kotlin
class LoginActivity : BasePermissionActivity() {
    override fun PermissionFactory.init() { // 待申请的权限
        +Manifest.permission.WRITE_EXTERNAL_STORAGE
        +Manifest.permission.CAMERA
        +Manifest.permission.READ_PHONE_STATE
    }

    override fun noGet(permission: String) { // 某个权限申请失败
        toast("$permission 请求失败!")
    }

    override fun reject(permission: String) { // 某个权限被带勾拒绝
        toast("$permission 请求被拒绝!")
    }

    override fun hadPermission(permission: String) {
        println("$permission 权限获取成功！")
    }
    ...
}
```

### FragmentExtend

直接在Fragment中调用

```kotlin
toast("123")

longToast("123")

startActivity<MainActivity>("key1" to "value1", "key2" to "value2") // 第二个参数action可选
```

### BaseFragment

BaseFragment和BaseActivity相同，大部分功能通过扩展函数分解了，为了代码问题，只提供了fragment中能安全调用view；

* Kotlin Fragment中要直接调用View的id必须在onViewCreated()之后，为保证代码不出问题，提供了initView()方法，在这个方法中可以直接调用id *；

### BaseRecyclerAdapter

重新封装了RecyclerAdapter, 可自定义Adapter继承BaseRecyclerAdapter，构造函数需要传入Context, List\<Bean\>, bingAction : (holder, bean) -\> Unit；绑定事件可选，但是不传入绑定事件必须重载onBindViewHolder方法；

使用示例

```kotlin
class SysMsgAdapter(mContext: Context, mSystemMsgBean: SystemMsgBean) : BaseRecyclerAdapter<SystemMsgBean.Result.Data>(
        mContext,
        mSystemMsgBean.result.dataList,
        { bean ->
            mTvTitle.text = Html.fromHtml(bean.MSG_ORIGIN)
            mTvContent.text = Html.fromHtml(bean.CONTENT)
            mTvTime.text = bean.CREATE_TIME
        }
) {
    override fun getItemView() = R.layout.item_sys_msg
}
```

### SimpleRecyclerAdapter

构建一个简易的RecyclerAdapter；

示例

```kotlin
SimpleRecyclerAdapter(mContext, R.layout.item_help, mBean.result.dataList, { bean ->
    mTvTitle.text = Html.fromHtml(bean.MSG_ORIGIN)
    mTvContent.text = Html.fromHtml(bean.CONTENT)
    mTvTime.text = bean.CREATE_TIME
})
```

---

## db

### annotation

- AnnotationExpression 解析数据库注解
- COL 数据库列，传入type，如TEXT，VARCHAR，INTEGER等
- FK 数据库外键，传入type、外键对应的表、外键对应的col
- PK 数据库主键，传入type
- TABLE 数据库表名，传入数据库表名

### CursorExtend

- toModel(clazz: Class<T>): T? // 转化为model
- toList(clazz: Class<T>): List<T>? //转化为List

### DBModel

> **需要存储在数据库的模型必须集成本类**

### DemoModel

```kotlin
@TABLE("user")
data class DemoModel (
    @PK("TEXT") val id: String, // id对应数据库id列
    @FK("TEXT", "org", "orgId") var deptId: String? = null,
    @COL("TEXT") val phone: String? = null,
    @COL("TEXT") val email: String? = null,
    @COL("TEXT") val username: String,
    @COL("TEXT") val name: String? = null
): DBModel()
```

### ModelExtend

- insert() 将模型保存到数据库
- update() 模型数据更新，主键不能为空
- delete() 删除模型，主键不能为空

### Select

> 数据库查询，构造函数可选择传入查询的列名

- table(table: String): Select *数据库表名*
- select(table: String): Select *查询的内容，如\*，'userId, username, password'列名集合, 默认查询全部\*。*
- where(sqlStr: String, vararg args: Any): Select *查询条件*
- and(sqlStr: String, vararg args: Any): Select *必须先有Where才能使用*
- or(sqlStr: String, vararg args: Any): Select *必须先有Where才能使用*
- limit(size: Int, offset: Int? = 0): Select *分页查询*
- sql(sqlStr: String, vararg args: Any): Select *直接写sql查询语句*
- desc(arg: String): Select *根据某列减序*
- asc(arg: String): Select *根据某列减序*
- join(sqlStr: String): Select *联表查询*
- <T> get(clazz: Class<T>): T? *获取当个数据模型*
- <T> find(clazz: Class<T>): List<T>? *获取符合条件的数据库列表*

### 数据库操作示例

```kotlin
/* 插入 */
val demoModel = DemoModel("123", "234", "18583872978", username = "qinyuanmao")
demoModel.insert()

/* 更新 */
val demoModel = DemoModel("123", "234", "17358591661", username = "qinyuanmao")
demoModel.update()

/* 查询 */
print(Select().table("user").where("[id] = ?", "123").get(DemoModel::class.java))

/* 删除 */
val demoModel = DemoModel("123", "234", "17358591661", username = "qinyuanmao")
demoModel.delete()

/* 多种查询条件 */
/* 普通查法 */
val user = Select("username, id").table("user")
            .where("name = ?", "秦源懋")
            .and("sex = ?", "男")
            .or("phone = ?", "18583872978")
            .get(DemoModel::class.java)
/* 自动初始化Select查找 */
val user = nGet<DemoModel> {
    table("user").where("[id] = ?", "123")
}
/* 中缀查法 */
val user = get<DemoModel> {
    this select "*" from "user" where "id" value "123"
}
```

### WisesoftDBHelper

> 数据库操作基类，需要传入

- Context，
- dbName: 数据库名称，
- mClazzs：需要对数据进行操作的model的List<Class<*>>
- dDBVersion: 数据库版本（可选，默认为1）

- doUpdate(sqlStr: String) *增删改走这个*
- doSelect(sqlStr: String): Cursor *查询走这个*

---

## utils

### ActivityContainer

Activity栈

- 初始化

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ActivityContainer.addActivity(this)
}
```
    
- 注销

```kotlin
override fun onDestroy() {
    super.onDestroy()
    ActivityContainer.removeActivity(this)
}
```

- 获取栈顶Activity

```kotlin
ActivityContainer.getTopActivity()
```

- 关闭所有Activity，适用于token失效

```kotlin
ActivityContainer.finishAllActivity()
```

### ObserverManager 观察者模式（类似与EventBus）

注意： post事件的时候，监听事件必须已定义，尽量少使用，过多使用会导致代码凌乱

- 设置监听

```kotlin
ObserverManager.add("Login"){ array ->
    // TODO
    // array[0] = arg1
    // array[1] = arg2
    // array[2] = arg3
}
```

- 执行事件

```kotlin
ObserverManager.post("Login", arg1, arg2, arg3) //第一个参数必填， 后面参数可选
```

- 删除监听

```kotlin
ObserverManager.delete("Login")
```

### ContextExtend

Context扩展函数，大部分Activity功能被转到这里

```kotlin
isNetworkAvailable() : Boolean //判断网络状态

isWhat() : String //判断Context类型

/*
返回类型：
Context.TYPE_ACTIVITY
Context.TYPE_APPLICATION
Context.TYPE_SERVICE
Context.TYPE_BROADCAST_RECEIVER
Context.TYPE_CONTENT_PROVIDER
Context.TYPE_OTHER
*/

isGpsOpen() : Boolean //判断当前Gps是否为高精度

getDeviceId() : String //获取设备唯一识别码

getDeviceBrand() : String //获取手机厂商

getSystemModel() : String //获取手机型号

getSystemVersion() : String //获取系统版本号

getLocalVersionName() : String //获取当前App版本

getLocalVersionCode() : Int //获取当前App版本号

getAssetsArray(fileName: String) : JSONArray //获取assets文件夹下的json文件并转成JSONArray

getAssetsObject(fileName: String) : JSONObject //获取assets文件夹下的json文件并转成JSONObject

initNotification(title: String, content: String, notifyId: Int, activity: Class\<T\>? /* 需要跳转的Activity */, icon: Int / * 图标ID * /) //显示通知

isNotificationEnabled() : Boolean //判断是否允许通知

createNotificationChannel() : String //Android8.0新权限，前台服务必须设置channelID

hintDialog(title: String? = null, message: String) : AlertDialog //显示提醒类dialog

showAlertDialog(title: String, message: String,
    cancelable: Boolean = true, //点击外部是否可以取消， 默认可以
    needCancel: Boolean = true, //是否需要关闭按钮，默认有
    positiveable: Boolean = true, //是否需要确认按钮， 默认有
    positiveTitle: String = "确定", //确认按钮文字， 默认"确定"
    cancelTitle: String = "关闭", //关闭按钮文字，默认"关闭"
    positiveAction: (dialog: DialogInterface) -> Unit = {}, //点击确认执行，默认关闭dialog，传入事件无需执行dismiss事件
    cancelAction: (dialog: DialogInterface) -> Unit = {} //点击取消执行，默认关闭dialog，传入事件无需执行dismiss事件
)

getProgressDialog(
    title: String? = null,
    message: String, 
    cancelable: Boolean = true //点击外部是否可以取消， 默认可以
)

ui{}

//demo
launch {
    //in thread
    ui { // Activity 和 Fragment有此方法
        // in Ui Thread
    }
}

toast(message: String)

longToast(message: String)

getView(viewId: Int, parent: ViewGroup? = null) // 通过ID查找View并返回View， 第二个参数用于fragment和adapter

startActvity<SomeoneActivity::class.java>("userId" to "abc", "username" to "qinyuanmao")
startService<SomeoneService::class.java>("userId" to "abc", "username" to "qinyuanmao")
```

### EditTextExtend

- hideInput() 隐藏键盘
- trimText() : String 返回去掉空格的text
- isNullText() : Boolean 判断是否为空字符串

### HashMapExtend

- toObject() : Serializable HashMap转为Serializable对象
- toStr() : String 输出key:value,key:value字符串，用于打印
- toJsonString() : String 输出JsonString

### ImageManager

图片加载

- getBitmap(url: String, action: (bitmap: Bitmap) -> Unit) //内部协程请求，并实现三级图片缓存

调用
```kotlin
ImageManager.INSTANCE.getBitmap("我是图片地址", {
    ui {
        it?.let {
            imageView.setImageBitmap(it)
        }
    }
})
```

### ImageViewExtend

- loadUrl(url)

### InputSteamExtend

- toStr() : String

- toBitmap() : Bitmap

### RecyclerViewExtend

- initVertical() : RecyclerView 初始化为垂直布局

- initHorizontal() : RecyclerView 初始化为水平布局

### SerializableExtend

- toMap() : HashMap<String, Any>

- toStr() : String //输出key:value,key:value字符串，用于打印

- toJsonStr() : String //输出JsonString

### StringExtend

- filterSpecChar() : String //去掉特殊字符

- toInputStream() : InputStream //string to inputStream

- base64ToBitmap() : Bitmap //base64 code to bitmap

- createPath(): Boolean //创建文件路径

- isUri() : Boolean // 是否为Uri

- isUrl() : Boolean // 是否为url

- "abc" has "a" : Boolean // 中缀函数，判断String中是否包含某个子串

- toJsonObject<User>() // JsonString 转化为Object，null已处理

- toJsonArray<User>() // jsonString 转化为List<User>，null已处理

### TaskManager

> 协程异步任务管理器

```kotlin
launch ("ThreadName") { // 普通异步任务
    // TODO 你想干啥
}
val job = delayTask (100， "ThreadName") { // 循环异步任务，job被cancel时会被关闭
    // TODO 你想干啥
}
async<User>("ThreadName") { // 带返回结果的异步任务
    // TODO 你想干啥
    // 耗时操作
    return user
}
repeatTask(100) { // 开多少个协程去干个事，举个例子，开100的协程去存储UserList
    // TODO 你想干啥
}
networkTask //参考网络请求
uploadTask //参考文件上传
downloadTask //参考文件下载
```

### WebViewExtend

- init() : WebView 初始化WebView

- progressChange(action: (Int) -> Unit) 监听进度变化


