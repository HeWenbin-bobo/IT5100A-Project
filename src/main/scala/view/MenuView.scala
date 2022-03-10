package view

import scala.concurrent.ExecutionContext.Implicits.global // support andThen
import scala.concurrent.duration._ // let future type could be run until time limit reaches
import cats.effect.{IO, IOApp}
import scala.util.{Try}
import cats.effect.unsafe.implicits._
import dao.{EmpDao, EmpDaoImpl}
import pojo.Emp._
import or._
import util.DBUtil

/**
 * 相比上版而言，使用了cats.effect.IO
 * 
 * @author He Wenbin
 *
 */
object MenuView extends IOApp.Simple {
	val dao: EmpDao = new EmpDaoImpl()

	def toInt(s: String): Try[Int] = Try(Integer.parseInt(s.trim)) //auxiliary function

	def printResult[T](results:List[T]) : IO[Unit] = {
		for {
			_ <- IO {results.foreach(result => result match {
						case (department_id, average_score) => println("%8d         %.2f".format(department_id, average_score))
						case (department_id, employee_id, employee_name, score) => println("%8d %12d %15s %8d".format(department_id, employee_id, employee_name, score))
						case _ => println(result)
					})
				}
		} yield()
	}

	def systemWelcome() : IO[Unit] = {
		for {
			_ <- IO(println("*************************************"))
			_ <- IO(println("**********Welcome to system**********"))
			_ <- IO(println("*************************************"))
		} yield ()
	}
	
	def showMenu() : IO[Unit] = {
		for {
			_ <- IO(println())
			_ <- IO(println("Please enter the number to get service"))
			_ <- IO(println("1. Add a new employee"))
			_ <- IO(println("2. View information of all employees"))
			_ <- IO(println("3. View information of all departments"))
			_ <- IO(println("4. Find employee by his/her Id"))
			_ <- IO(println("5. Update salary of an employee"))
			_ <- IO(println("6. Delete an employee"))
			_ <- IO(println("7. Query the department info by employee Id"))
			_ <- IO(println("8. View information of all employee performance"))
			_ <- IO(println("9. Add a new employee performance report"))
			_ <- IO(println("10. Find average performance score of an employee"))
			_ <- IO(println("11. Find average performance score of each department"))
			_ <- IO(println("12. Find employee with highest performance score for each department"))
			_ <- IO(println("13. Exit"))
			service <- IO.readLine          
			_ <- 
			(service match {
				case "1" =>
					addEmployee() >> showMenu()
				case "2" =>
					show_employee_table() >> showMenu()
				case "3" =>
					show_department_table() >> showMenu()
				case "4" => 
					find_employee_by_id() >> showMenu()
				case "5" =>
					update_salary_of_employee() >> showMenu()
				case "6" =>
					delete_employee() >> showMenu()
				case "7" =>
					get_department_info_by_employee_id() >> showMenu()
				case "8" =>
					show_performance_table() >> showMenu()
				case "9" =>
					addPer_Report() >> showMenu()
				case "10" =>
					emp_avg_score() >> showMenu()
				case "11" =>
					deprt_avg_score() >> showMenu()
				case "12" =>
					find_best_perform_employee() >> showMenu()
				case "13" =>
					IO(println("Thank you for using,byebye~"))
				case _ => { 
					IO(println("Something wrong with your input, please re-enter")) >> showMenu()
				}
			}
			)
		} yield ()
	}

	def delete_employee() : IO[Unit] = {
		for {
			employee_id <- IO.println("Please enter Id of employee:") >> IO.readLine
			_ <- dao.delete_employee(employee_id.toInt) match {
				case Left(e) => IO.println(e.getMessage)
				case Right(result) => if (result > 0) IO.println("Successfully delete") else IO.println("Did not exist!")
			}
		} yield()
	}

	def update_salary_of_employee() : IO[Unit] = {
		for {
			_ <- IO.println("Please enter Id of employee:")
			employee_id <- IO.readLine
			_ <- IO.println("Please enter Salary of employee:")
			salary <- IO.readLine
			_ <- dao.update_salary_of_employee(employee_id.toInt, salary.toInt) match {
				case Left(e) => IO.println(e.getMessage)
				case Right(result) => if (result > 0) IO.println("Successfully update") else IO.println("Did not exist!")
			}
		} yield ()
	}

	def find_employee_by_id() : IO[Unit] = {
		for {
			_ <- IO.println("Please enter Id of employee:")
        	employee_id <- IO.readLine
			_ <- dao.find_employee_by_id(employee_id.toInt) match {
				case Left(e) => IO.println(e.getMessage)
				case Right(result) => if (result.length > 0) printResult(result.toList) else IO.println("Did not exist!")
			}
		} yield ()
	}

	def get_department_info_by_employee_id() : IO[Unit] = {
		for {
			_ <- IO.println("Please enter Id of employee:")
        	employee_id <- IO.readLine
			_ <- dao.get_department_info_by_employee_id(employee_id.toInt) match {
				case Left(e) => IO.println(e.getMessage)
				case Right(result) => if (result.length > 0) printResult(result.toList) else IO.println("Did not exist!")
			}
		} yield ()
	}

	def show_employee_table() : IO[Unit] = {
		for {
			_ <- dao.show_employee_table() match {
				case Left(e) => IO.println(e.getMessage)
				case Right(result) => if (result.length > 0) printResult(result.toList) else IO.println("Empty database!")
			}
		} yield ()
	}

	def show_department_table() : IO[Unit] = {
		for {
			_ <- dao.show_department_table() match {
				case Left(e) => IO.println(e.getMessage)
				case Right(result) => if (result.length > 0) printResult(result.toList) else IO.println("Empty database!")
			}
		} yield ()
	}

	def show_performance_table() : IO[Unit] = {
		for {
			_ <- dao.show_performance_table() match {
				case Left(e) => IO.println(e.getMessage)
				case Right(result) => if (result.length > 0) printResult(result.toList) else IO.println("Empty database!")
			}
		} yield ()
	}
    
	def addEmployee() : IO[Unit] = {
		for {
			employee_id <- IO.println("Please enter Id of employee:") >> IO.readLine
			employee_name <- IO.println("Please enter Name of employee:") >> IO.readLine
			job <- IO.println("Please enter Job of employee:") >> IO.readLine
			age <- IO.println("Please enter Age of employee:") >> IO.readLine
			salary <- IO.println("Please enter Salary of employee:") >> IO.readLine
			department_id <- IO.println("Please enter Department Id of employee:") >> IO.readLine
			empInfo: Seq[EmpInfo] = Seq(EmpInfo(employee_name, age.toInt, job, salary.toInt, department_id.toInt, employee_id.toInt))
			_ <- dao.addEmployee(empInfo) match {
				case Left(e) => IO.println(e.getMessage)
				case Right(_) => IO.println("Successfully add!")
			}
		} yield ()
	}

	def addPer_Report() : IO[Unit] = {
		for {
			employee_id <- IO.println("Please enter Id of employee you want add report to:")  >> IO.readLine
			department_id <- IO.println("Please enter Department Id of employee you want add report to:") >> IO.readLine
			per_rating <- IO.println("Please enter the score for his/her performance:") >> IO.readLine
			per_report <- IO.println("Please provide comments for this evaluation:") >> IO.readLine
			empPer: Seq[EmpPer] = Seq(EmpPer(employee_id.toInt, department_id.toInt, per_rating.toInt, per_report))
			_ <- dao.addPer_Report(empPer) match {
				case Left(e) => IO.println(e.getMessage)
				case Right(_) => IO.println("Successfully add!")
			}
		} yield ()
	}

	def emp_avg_score() : IO[Unit] = {
		for {
			employee_id <- IO.println("Please enter Id of employee:") >> IO.readLine
			_ <- dao.emp_avg_score(employee_id.toInt) match {
				case Left(e) => IO.println(e.getMessage)
				case Right(result) => IO.println(s"The average score is ${result}")
			}
		} yield ()
	}

	def deprt_avg_score() : IO[Unit] = {
		for {
			_ <- dao.deprt_avg_score() match {
				case Left(e) => IO.println(e.getMessage)
				case Right(result) => if (result.length > 0) printResult(result.toList) else IO.println("Empty database!")
			}
		} yield ()
	}

	def find_best_perform_employee() : IO[Unit] = {
		for {
			_ <- dao.find_best_perform_employee() match {
				case Left(e) => IO.println(e.getMessage)
				case Right(result) => if (result.length > 0) printResult(result.toList) else IO.println("Empty database!")
			}
		} yield ()
	}

	val run = {
    for {
      // Initial Steps:
      _ <- systemWelcome()
      _ <- IO.sleep(1.seconds) >> showMenu() >> DBUtil.closeAll()
    } yield ()
  }
}