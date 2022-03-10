package dao

import slick.jdbc.MySQLProfile.api._ // related functions to support the connection with mysql
import scala.concurrent.ExecutionContext.Implicits.global // support andThen
import or._
import pojo.Emp._
import util.DBUtil

/**
 * 相比上版而言，加入了or特性
 * 
 * @author He Wenbin
 *
 */
class EmpDaoImpl extends EmpDao {
    //val db = DBUtil.getConnection()
	DBUtil.exec(init)

	override def show_employee_table() : or[Error,Seq[EmpInfo]] = {
		val temp: DBIO[Seq[EmpInfo]] = employee.result
		DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("show_employee_table " + e.getMessage))
			case Right(result) => Right(result)
		}
	}

	override def show_department_table() : or[Error,Seq[DepartmentInfo]] = {
		val temp: DBIO[Seq[DepartmentInfo]] = department.result
		DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("show_department_table " + e.getMessage))
			case Right(result) => Right(result)
		}
	}

	override def show_performance_table() : or[Error,Seq[EmpPer]] = {
		val temp: DBIO[Seq[EmpPer]] = emp_performance.result
		DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("show_performance_table " + e.getMessage))
			case Right(result) => Right(result)
		}
	}

	override def find_employee_by_id(employee_id: Int) : or[Error,Seq[EmpInfo]] = {
		val temp: DBIO[Seq[EmpInfo]] = employee.filter(_.employee_id === employee_id).result
        DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("find_employee_by_id " + e.getMessage))
			case Right(result) => Right(result)
		}
	}

	override def addEmployee(empInfo: Seq[EmpInfo]) : or[Error,Option[Int]] = {
		val temp: DBIO[Option[Int]] = employee ++= empInfo
        DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("addEmployee " + e.getMessage))
			case Right(result) => Right(result)
		}
	}

	override def addPer_Report(empPer: Seq[EmpPer]) : or[Error,Option[Int]] = {
		val temp: DBIO[Option[Int]] = emp_performance ++= empPer
        DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("addPer_Report " + e.getMessage))
			case Right(result) => Right(result)
		}
	}

    override def update_salary_of_employee(employee_id: Int, salary: Int) : or[Error,Int] = {
		val temp: DBIO[Int] = employee.filter(_.employee_id === employee_id).map(_.salary).update(salary)
        DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("update_salary_of_employee " + e.getMessage))
			case Right(result) => Right(result)
		}
	}

	override def get_department_info_by_employee_id(employee_id: Int) : or[Error,Seq[DepartmentInfo]] = {
		val temp: DBIO[Seq[Int]] = employee.filter(_.employee_id === employee_id).map(_.department_id).result
        val employeeResult = DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("get_department_info_by_employee_id " + e.getMessage))
			case Right(result) => Right(result)
		}
		employeeResult match {
			case Left(e : Error) => Left(e)
			case Right(id) => DBUtil.exec(department.filter(_.department_id === id(0)).result) match {
				case Left(e) => Left(new Error("get_department_info_by_employee_id " + e.getMessage))
				case Right(result) => Right(result)
			}
		}

	}

    override def delete_employee(employee_id: Int) : or[Error,Int] = {
		val temp: DBIO[Int] = emp_performance.filter(_.employee_id === employee_id).delete
		DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("delete_employee " + e.getMessage))
			//case Right(result) => Left(new Error("delete_employee Failed"))
			case Right(result) => DBUtil.exec(employee.filter(_.employee_id === employee_id).delete) match {
				case Left(e) => Left(new Error("delete_employee " + e.getMessage))
				case Right(result) => Right(result)
			} 
		}
	}

	override def emp_avg_score(employee_id : Int) : or[Error,Double] = {
		val temp = emp_performance.filter(_.employee_id === employee_id).result
		DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("emp_avg_score " + e.getMessage))
			case Right(result) if (result.length != 0) => Right((result.map(_.Per_Rating).fold(0)((x,y) => x + y)) / result.length)
			case Right(result) if (result.length == 0) => Left(new Error("emp_avg_score " + "Cannot find the employee"))
		}
	}

	override def deprt_avg_score() : or[Error,Seq[(Int,Double)]] = {
		val temp = sql"""select p.department_id,avg(p.per_rating) from performance p group by p.department_id""".as[(Int,Double)]
		DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("deprt_avg_score " + e.getMessage))
			case Right(result) => Right(result)
		}
	}

	override def find_best_perform_employee() : or[Error,Seq[(Int,Int,String,Int)]] = {
		val temp = sql"""select tempt.employee_id, tempt.department_id, e.employee_name, max(tempt.rating)
							from (select p.employee_id,p.department_id, avg(p.per_rating) as rating
							from performance p
							group by p.employee_id, p.department_id) as tempt, employee e
							where tempt.employee_id = e.employee_id
							group by tempt.employee_id, e.employee_name, tempt.department_id""".as[(Int,Int,String,Int)]
		DBUtil.exec(temp) match {
			case Left(e) => Left(new Error("find_best_perform_employee " + e.getMessage))
			case Right(result) => Right(result)
		}
	}

}