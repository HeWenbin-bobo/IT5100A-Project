package pojo

import slick.jdbc.MySQLProfile.api._ // related functions to support the connection with mysql
import scala.concurrent.ExecutionContext.Implicits.global // support andThen

// Define table structure
object Emp
{    
    case class DepartmentInfo(department_id: Int, department_name: String, location_id: Int)
    class Department_table(tag: Tag) extends Table[DepartmentInfo](tag, "department")
    {

        def department_id = column[Int]("department_id", O.PrimaryKey)
        def department_name = column[String]("department_name")
        def location_id = column[Int]("location_id")

        def * = (department_id, department_name, location_id).mapTo[DepartmentInfo]

    }
    lazy val department = TableQuery[Department_table]



    case class EmpInfo(employee_name: String, age: Int, job: String, salary: Int, department_id: Int, employee_id: Int = 0)

    class Employee_table(tag: Tag) extends Table[EmpInfo](tag, "employee")
    {
        def employee_id = column[Int]("employee_id", O.PrimaryKey, O.AutoInc)
        def employee_name = column[String]("employee_name")
        def age = column[Int]("age")
        def job = column[String]("job")
        def salary = column[Int]("salary")
        def department_id = column[Int]("department_id")
        def department_id_fk = foreignKey("Dprt_ID_FK", department_id, department)(_.department_id, onUpdate=ForeignKeyAction.Cascade,onDelete=ForeignKeyAction.Cascade)

        def * = (employee_name, age, job, salary, department_id, employee_id).mapTo[EmpInfo]

    }
    lazy val employee = TableQuery[Employee_table]


    case class EmpPer(employee_id: Int, department_id: Int, Per_Rating: Int, Per_report: String, report_id: Int = 0 )

    class Per_table(tag: Tag) extends Table[EmpPer](tag, "performance")
    {
        def employee_id = column[Int]("employee_id")
        def report_id = column[Int]("report_id", O.PrimaryKey, O.AutoInc)
        def department_id = column[Int]("department_id")
        def per_rating = column[Int]("per_rating")
        def per_report = column[String]("per_report")
        //def pk = primaryKey("pk_per", (report_id)) 
        
        def employee_id_fk = foreignKey("Emp_ID_FK", employee_id, employee)(_.employee_id, onUpdate=ForeignKeyAction.Cascade,onDelete=ForeignKeyAction.Cascade) 
        def department_id_fk = foreignKey("Dprt_ID_FK2", department_id, department)(_.department_id, onUpdate=ForeignKeyAction.Cascade,onDelete=ForeignKeyAction.Cascade)

        def * = (employee_id, department_id,per_rating, per_report,report_id).mapTo[EmpPer]

    }

    lazy val emp_performance = TableQuery[Per_table]

    // Initialization of databases
    def init =
    DBIO.seq(

    sqlu"""SET FOREIGN_KEY_CHECKS = 0""",
    sqlu"""drop table if exists employee""",
    sqlu"""drop table if exists department""",
    sqlu"""drop table if exists performance""",
    sqlu"""SET FOREIGN_KEY_CHECKS = 1""",

    (employee.schema ++ department.schema ++ emp_performance.schema).create,

    department ++= Seq(
        DepartmentInfo(10, "IT", 320),
        DepartmentInfo(20, "Cloths", 230),
        DepartmentInfo(30, "SHIS", 220),
        DepartmentInfo(55, "MATH", 290),
        DepartmentInfo(60, "Comp", 100)),

    employee ++= Seq(EmpInfo("Mary", 18,  "Calculate",  1800,  10),
        EmpInfo("Michael", 25,  "Speak",  1600,  20),
        EmpInfo("Leo", 27,  "Jump",  1800,  30)),
    
    emp_performance ++= Seq(
        EmpPer(1,10,88,"Well done in the taskA"),
        EmpPer(1,10,60,"a lot of room for improvements in taksB"),
        EmpPer(2,20,100,"Performed perfectly in taksC"),
        EmpPer(2,20,100,"Performed perfectly in taksD")
        )
    )
}