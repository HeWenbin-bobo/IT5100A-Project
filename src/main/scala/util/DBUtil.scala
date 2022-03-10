package util

import java.io.{File, IOException, InputStream, BufferedInputStream, FileInputStream}
import java.sql.SQLException
import java.util.Properties
import slick.jdbc.MySQLProfile.api._ // related functions to support the connection with mysql
import scala.concurrent.Await // let the execution block until it completes
import scala.concurrent.ExecutionContext.Implicits.global // support andThen
import scala.concurrent.duration._ // let future type could be run until time limit reaches
import org.apache.commons.lang3.StringUtils // 提供了丰富的字符串操作方法
import cats.effect.IO
import or._

object DBUtil {
    // 对于全局变量,可以用 _ 来表示默认初始值
    var driver:String = _
    var url:String = _
    var user:String = _
    var password:String = _
    var connectionPool:String = "disabled"
    loadConfig()
    var db = this.getConnection()

    // 读取属性文件properties并获取内容,返回值为Unit
    def loadConfig () : Unit = {
        // 准备一个空的map，没有key-value(没有找到scala的代替方法)
        val prop:Properties = new Properties
        
        // 读取文件，并将文件键值对存入Properties对象，指向resource文件夹下的文件
        // getClass是获得传入的变量的class类型(可以改成this.getClass)
        // getClassLoader是获取其类加载器（相当于确定了起始路径）
        // getResourceAsStream查找传入的文件，并获取其内容返回成流
        // .properties文件中变量的值不用加""，而.conf需要加""，其他没区别
        val is = DBUtil.getClass.getClassLoader.getResourceAsStream("application.properties") //classpath
        
        // 判断实际读取文件
        loadCurrPathProp(prop: Properties)

        // 尝试读入，如果不成功就抛出IOException,此时将print出提示
        try {
            prop.load(is);
        } catch {
            case _: IOException => IO.println("IO Exception")
        }

        // 从prop中根据key获取五个参数的值
        // 实际可能为空(null)，按道理这里应该检查下，比如if (driver == null) throw new NullPointerException("...")
        // trim()的作用是取出字符串两端多余的字符或者是其他预定义字符
        driver = prop.getProperty("driver").trim()
        url = prop.getProperty("url").trim()
        user = prop.getProperty("user").trim()
        password = prop.getProperty("password").trim()
        connectionPool = prop.getProperty("connectionPool").trim()
        
        // 加载驱动，实际是判断是否有这个class(即是否正确import)
        try {
            println(driver)
            Class.forName(driver) //returns Class[driver所代表的class name]
        } catch {
            case _: ClassNotFoundException => IO.println("No such class, please check!")
        }
    }
    
    /**
     * 加载当前工程目录下的application.properties文件,其优先级更高
     * 即该文件内容若与resource文件夹下的的application.properties文件内容冲突, 则最终读取该文件相应的内容
     * 设置成private是因为不能让其在class外进行访问，因为只是判断读取哪个application.properties而已
     * 调用loadConfig时则自动调用本function
     */
    private def loadCurrPathProp(prop: Properties): Unit = {
        val osName = System.getProperty("os.name")
        var fileName: String = null
        // containsIgnoreCase检查给定字符串是否包含作为第二个字符串，且忽略大小写问题
        // 根据结果构造出当前工程目录下的application.properties文件路径
        if (StringUtils.containsIgnoreCase(osName, "Windows")) {
            fileName = System.getProperty("user.dir") + "\\application.properties"
        } else {
            fileName = System.getProperty("user.dir") + "/application.properties"
        }
        
        // File对象代表磁盘中实际存在的文件和目录
        val file = new File(fileName)
        // exists测试此抽象路径名表示的文件或目录是否存在
        if (file.exists) {
            // 路径表示方法跟getClass.getClassLoader......不同，所以用了不同的处理方式
            // FileInputStream就是读入文件数据，若文件较大，就要用BufferedInputStream来提高性能
            // BufferedInputStream(byte)提高了字节(byte)文件的内容读取的性能
            val in = new BufferedInputStream(new FileInputStream(file))
            // 把内容读到prop中
            try {
            prop.load(in);
            } catch {
                case _: IOException => IO.println("IO Exception")
            }
        }
    }

    /**
     * 获取数据库连接
     */
    def getConnection() : slick.jdbc.MySQLProfile.backend.Database = {
        var db: slick.jdbc.MySQLProfile.backend.Database = null
        try{  			
            // 建立数据库连接,实际用Database.forconfig舒服点
            // forURL(url: String, user: String = null, password: String = null, prop: Properties = null, driver: String = null, ...)	
            db = Database.forURL(url, Map("driver"->driver, "user"->user, "password"->password, "connectionPool"->connectionPool))
        } catch {
            case _: SQLException => IO.println("SQL Exception")
        }
        db
    }
    
    /**
     * 关闭数据库资源
     */
    def closeAll(): IO[Unit] = {
        //关闭数据库资源        
        try {
            db.close()
            for {
                _ <- IO.println("Successfully close the database!")
            } yield() 
        } catch {
            case _ : Throwable => for {
                _ <- IO.println("Cannot close the database!")
            } yield()
        }
    }

    // 执行传入的sql命令
    def exec[T](program: DBIO[T]): or[Error, T] = {
        try {
            Right(Await.result(db.run(program), 2.seconds))
        } catch {
            case e : Throwable => Left(new Error("query failed " + e.getMessage)) // Don't print anything
        }
    }
}