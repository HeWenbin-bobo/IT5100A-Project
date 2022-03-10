package dao

import slick.jdbc.MySQLProfile.api._ // related functions to support the connection with mysql
import scala.concurrent.ExecutionContext.Implicits.global // support andThen
import pojo.Emp._
import util.DBUtil
import or._

/**
 * 专门用来操作emp的接口
 * @author He Wenbin
 *
 */
trait EmpDao {
	
	/**
	 * 查询所有员工信息(select all)
	 * @return Seq[EmpInfo]
	 */
	def show_employee_table() : or[Error,Seq[EmpInfo]]//通过Seq来存储EmpInfo类型的数据

	/**
	 * 查询所有部门信息(select all)
	 * @return Seq[DepartmentInfo]
	 */
	def show_department_table() : or[Error,Seq[DepartmentInfo]]//通过Seq来存储DepartmentInfo类型的数据

	/**
	 * 查询所有员工表现(select all)
	 * @return Seq[EmpPer]
	 */
	def show_performance_table() : or[Error,Seq[EmpPer]]//通过Seq来存储EmpPer类型的数据
	
	/**
	 * 根据员工编号查询员工信息(select by employee_id)
	 * @param employee_id
	 * @return
	 */
	def find_employee_by_id(employee_id: Int) : or[Error,Seq[EmpInfo]]
	
	/**
	 * 新增员工信息(insert)
	 * @param Seq[EmpInfo]
	 * @return
	 */
	def addEmployee(empInfo : Seq[EmpInfo]) : or[Error,Option[Int]]

	/**
	 * 新增员工报告(insert)
	 * @param Seq[EmpPer]
	 * @return
	 */
	def addPer_Report(empPer : Seq[EmpPer]) : or[Error,Option[Int]]
	
	/**
	 * 根据编号修改工资（update sal by employee_id）
	 * @param employee_id
	 * @param salary
	 * @return
	 */
	def update_salary_of_employee(employee_id : Int, salary : Int) : or[Error,Int]

	/**
	 * 根据员工编号查询部门信息(select by employee_id)
	 * @param employee_id
	 * @return
	 */
	def get_department_info_by_employee_id(employee_id: Int) : or[Error,Seq[DepartmentInfo]]
	
	/**
	 * 根据编号删除员工
	 * @param employee_id
	 * @return
	 */
	def delete_employee(employee_id : Int) : or[Error,Int]

	/**
	 * 根据编号求出员工平均分数
	 * @param employee_id
	 * @return
	 */
	def emp_avg_score(employee_id : Int) : or[Error,Double]

	/**
	 * 求出所有部门的平均分数
	 * @return
	 */
	def deprt_avg_score() : or[Error,Seq[(Int,Double)]]

	/**
	 * 找出最好的员工
	 * @return
	 */
	def find_best_perform_employee() : or[Error,Seq[(Int,Int,String,Int)]]
}