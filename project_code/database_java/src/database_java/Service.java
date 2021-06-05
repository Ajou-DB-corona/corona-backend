package database_java;

import java.sql.*;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.nio.file.Files;

class courseCriteriaObj {
	int categoryID;
	int num;

	public courseCriteriaObj(int categoryID, int num) {
		this.categoryID = categoryID;
		this.num = num;
	}
}

public class Service {
	private User user = null;
	private Connection conn = null;
	private Statement st = null;

	public Service() {
		String categoryPath = "./table/Category.csv";
		String cityPath = "./table/City.csv";
		String placePath = "./table/Place.csv";
		String placeLocationPath = "./table/PlaceLocation.csv";
		String starPath = "./table/Star.csv";
		String line = null;
		String q = "";
		boolean firstRead = true;
		BufferedReader br = null;

		Scanner scan = new Scanner(System.in);
		String userID = null;

		System.out.format("userID 입력 : ");
		userID = scan.next();
		scan.nextLine();

		this.user = new User(new String(userID));
		System.out.format("%s님 환영합니다!\n\n", user.getUserID());
		// crate table or read table
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "0000");
			st = conn.createStatement();
			// create category table
			br = Files.newBufferedReader(Paths.get(categoryPath));

			while ((line = br.readLine()) != null) {
				line = line.replace("\uFEFF", "");
				String[] str = line.split(",");
				if (firstRead) {
					q = "create table Category(" + str[0] + " int," + str[1] + " varchar(10),primary key(" + str[0]
							+ "));";
					st.execute(q);
				} else {
					st.execute("insert into Category values(" + str[0] + ",'" + str[1] + "');");
				}
				firstRead = false;
			}
		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println(ex);
		}
		try {
			firstRead = true;
			br = Files.newBufferedReader(Paths.get(cityPath));
			while ((line = br.readLine()) != null) {
				line = line.replace("\uFEFF", "");
				String[] str = line.split(",");
				if (firstRead) {
					q = "create table City(" + str[0] + " int," + str[1] + " varchar(10)," + str[2] + " float," + str[3]
							+ " float," + "primary key(" + str[0] + "));";
					st.execute(q);
				} else {
					st.execute(
							"insert into City values(" + str[0] + ",'" + str[1] + "'," + str[2] + "," + str[3] + ");");
				}
				firstRead = false;
			}
		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println(ex);
		}
		try {
			firstRead = true;
			br = Files.newBufferedReader(Paths.get(placePath));
			while ((line = br.readLine()) != null) {
				line = line.replace("\uFEFF", "");
				String[] str = line.split(",");
				if (firstRead) {
					st.execute("create table Place(" + str[0] + " int," + str[1] + " varchar(30)," + str[2] + " int,"
							+ str[3] + " varchar(30)," + str[4] + " varchar(60)," + str[5] + " int," + "primary key("
							+ str[2] + "));");
				} else {
					q = "insert into Place values(" + str[0] + ",'" + str[1] + "'," + str[2] + ",'" + str[3] + "','"
							+ str[4] + "'," + str[5] + ");";
					st.execute(q);

				}
				firstRead = false;
			}
		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println(ex);
		}
		try {
			firstRead = true;
			br = Files.newBufferedReader(Paths.get(placeLocationPath));
			while ((line = br.readLine()) != null) {
				line = line.replace("\uFEFF", "");
				String[] str = line.split(",");

				if (str.length <= 2) {
					String tmp = new String(str[0]);
					str = new String[3];
					str[0] = new String(tmp);
					str[1] = "null";
					str[2] = "null";

				} else if (str[1].equals("") || str[2].equals("")) {
					String tmp = new String(str[0]);
					str = new String[3];
					str[0] = new String(tmp);
					str[1] = "null";
					str[2] = "null";

				}
				if (firstRead) {
					st.execute("create table PlaceLocation(" + str[0] + " int," + str[1] + " float," + str[2]
							+ " float," + "primary key(" + str[0] + "));");
				} else {
					st.execute("insert into PlaceLocation values(" + str[0] + "," + str[1] + "," + str[2] + ");");
				}
				firstRead = false;
			}
		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println(ex);
		}
		try {
			firstRead = true;
			br = Files.newBufferedReader(Paths.get(starPath));
			while ((line = br.readLine()) != null) {
				line = line.replace("\uFEFF", "");
				String[] str = line.split(",");
				if (firstRead) {
					st.execute("create table Star(" + str[0] + " varchar(10)," + str[1] + " int," + str[2] + " int);");
				} else {
					if (str.length == 2) {
						String tmp = new String(str[1]);
						str = new String[3];
						str[0] = "null";
						str[1] = new String(tmp);
						str[2] = "null";
					}

					st.execute("insert into Star values('" + str[0] + "'," + str[1] + "," + str[2] + ");");

				}
				firstRead = false;
			}
		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	public void setUserPos(int cityID) {
		user.setPos(cityID);
	}

	public void setUserID(String userID) {
		user.setUserID(new String(userID));
	}

	public boolean checkCategory(int categoryID) throws SQLException {
		ResultSet ret = st.executeQuery("select count(*) from Category where categoryID=" + categoryID + ";");
		while (ret.next()) {
			int tmp = Integer.parseInt(ret.getString(1));
			if (tmp == 0) {
				return false;
			}
			break;
		}
		return true;
	}

	public int checkCity(String city) throws SQLException { // if not, return -1
		int cityID = -1;

		ResultSet ret = st.executeQuery("select count(*) from city where city='" + city + "';");
		while (ret.next()) {
			int tmp = Integer.parseInt(ret.getString(1));
			if (tmp == 0) {
				return -1;
			}
			ret = st.executeQuery("select cityID from city where city='" + city + "';");
			ret.next();
			cityID = Integer.parseInt(ret.getString(1));
			break;
		}
		return cityID;
	}

	public void readCategoryPlace(int categoryID, boolean orderbyStar) {
		ResultSet ret = null;
		ResultSetMetaData retMeta = null;

		try {

			if (!checkCategory(categoryID)) {
				throw new InputMismatchException("없는 카테고리");
			}

			if (orderbyStar) {

				ret = st.executeQuery(
						"select category,placename,number,address,count,star from place natural join category natural join (select customerid,placeid,count(star), round(avg(star)::numeric, 1) as star\r\n"
								+ "from star\r\n" + "group by rollup(placeid,customerid))as S\r\n"
								+ "where customerid is null and categoryid=" + categoryID + "\r\n"
								+ "union						\r\n"
								+ "select category,placename,number,address,0,0\r\n"
								+ "from place natural join category\r\n"
								+ "where placeid not in(select placeid from star) and categoryid=" + categoryID
								+ " order by star desc;");
			} else {
				ret = st.executeQuery(
						"select category,placename,number,address,count,star from place natural join category natural join (select customerid,placeid,count(star), round(avg(star)::numeric, 1) as star\r\n"
								+ "from star\r\n" + "group by rollup(placeid,customerid))as S\r\n"
								+ "where customerid is null and categoryid=" + categoryID + "\r\n"
								+ "union						\r\n"
								+ "select category,placename,number,address,0,0\r\n"
								+ "from place natural join category\r\n"
								+ "where placeid not in(select placeid from star) and categoryid=" + categoryID + ";");
			}

			retMeta = ret.getMetaData();
			printTable(ret, retMeta);

		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (InputMismatchException ex) {
			System.out.println(ex);
		}
	}

	public void readCityPlace(String city, boolean orderbyStar) {
		ResultSet ret = null;
		ResultSetMetaData retMeta = null;
		int cityID = 0;

		try {
			cityID = checkCity(city);
			if (cityID == -1) {
				throw new InputMismatchException("없는 도시.");
			}

			if (orderbyStar) {
				ret = st.executeQuery(
						"select category,placename,number,address,count,star from place natural join category natural join city natural join (select customerid,placeid,count(star), round(avg(star)::numeric, 1) as star\r\n"
								+ "from star\r\n" + "group by rollup(placeid,customerid))as S\r\n"
								+ "where customerid is null and cityid=" + cityID + "\r\n"
								+ "union						\r\n"
								+ "select category,placename,number,address,0,0\r\n"
								+ "from place natural join category natural join city\r\n"
								+ "where placeid not in(select placeid from star) and cityid=" + cityID
								+ " order by star desc;");
			} else {
				ret = st.executeQuery(
						"select category,placename,number,address,count,star from place natural join category natural join city natural join (select customerid,placeid,count(star), round(avg(star)::numeric, 1) as star\r\n"
								+ "from star\r\n" + "group by rollup(placeid,customerid))as S\r\n"
								+ "where customerid is null and cityid=" + cityID + "\r\n"
								+ "union						\r\n"
								+ "select category,placename,number,address,0,0\r\n"
								+ "from place natural join category natural join city\r\n"
								+ "where placeid not in(select placeid from star) and cityid=" + cityID + ";");
			}

			retMeta = ret.getMetaData();
			printTable(ret, retMeta);

		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (InputMismatchException ex) {
			System.out.println(ex);
		}
	}

	public void readCategoryCityPlace(String city, int categoryID, boolean orderbyStar) {
		ResultSet ret = null;
		ResultSetMetaData retMeta = null;
		int cityID = 0;

		try {
			cityID = checkCity(city);
			if (cityID == -1) {
				throw new InputMismatchException("없는 도시.");
			}

			if (!checkCategory(categoryID)) {
				throw new InputMismatchException("없는 카테고리");
			}

			if (orderbyStar) {
				ret = st.executeQuery(
						"select category,placename,number,address,count,star from place natural join category natural join city natural join (select customerid,placeid,count(star), round(avg(star)::numeric, 1) as star\r\n"
								+ "from star\r\n" + "group by rollup(placeid,customerid))as S\r\n"
								+ "where customerid is null and cityid=" + cityID + " and categoryid=" + categoryID
								+ "\r\n" + "union						\r\n"
								+ "select category,placename,number,address,0,0\r\n"
								+ "from place natural join category natural join city\r\n"
								+ "where placeid not in(select placeid from star) and cityid=" + cityID
								+ " and categoryid=" + categoryID + " order by star desc;");
			} else {
				ret = st.executeQuery(
						"select category,placename,number,address,count,star from place natural join category natural join city natural join (select customerid,placeid,count(star), round(avg(star)::numeric, 1) as star\r\n"
								+ "from star\r\n" + "group by rollup(placeid,customerid))as S\r\n"
								+ "where customerid is null and cityid=" + cityID + " and categoryid=" + categoryID
								+ "\r\n" + "union						\r\n"
								+ "select category,placename,number,address,0,0\r\n"
								+ "from place natural join category natural join city\r\n"
								+ "where placeid not in(select placeid from star) and cityid=" + cityID
								+ " and categoryid=" + categoryID + ";");
			}

			retMeta = ret.getMetaData();
			printTable(ret, retMeta);

		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (InputMismatchException ex) {
			System.out.println(ex);
		}
	}

	public void readCoursePlace(String srcCity, String dstCity, int courcePlaceNum, ArrayList<courseCriteriaObj> courseCriteria) {
		courseCriteria.forEach(item->System.out.println(item.categoryID + " " + item.num));
		/*
		 * int srcCityID=0,dstCityID=0;
		 * 
		 * float[] latitude = new float[2]; float[] longitude = new float[2]; float
		 * centerLatitude=0,centerLongitude=0;
		 * 
		 * float city[][] = null; int cityNum =0; boolean checkDist[] = null;
		 * 
		 * ResultSet ret = null; ResultSetMetaData retMeta = null;
		 * 
		 * try {
		 * 
		 * srcCityID = checkCity(srcCity); if(srcCityID==-1) { throw new
		 * InputMismatchException("출발 도시가 없는 도시입니다.") ; }
		 * 
		 * 
		 * dstCityID = checkCity(dstCity); if(dstCityID==-1) { throw new
		 * InputMismatchException("도착 도시가 없는 도시입니다.") ; }
		 * 
		 * 
		 * ret = st.executeQuery("select latitude, longitude from city where cityid="
		 * +dstCityID+" or cityid="+srcCityID+";"); int j = 0; while(ret.next()) {
		 * latitude[j] = Float.parseFloat(ret.getString(1)); longitude[j++] =
		 * Float.parseFloat(ret.getString(2));
		 * 
		 * 
		 * }
		 * 
		 * ret = st.executeQuery("select count(*) from city;"); while(ret.next()) {
		 * cityNum = Integer.parseInt(ret.getString(1)); city= new float[2][cityNum];
		 * checkDist = new boolean[cityNum]; break; }
		 * 
		 * 
		 * ret = st.executeQuery("select latitude, longitude from city;"); j = 0;
		 * while(ret.next()) { city[0][j] = Float.parseFloat(ret.getString(1));
		 * city[1][j++] = Float.parseFloat(ret.getString(2)); } centerLatitude =
		 * (latitude[0]+latitude[1])/2; centerLongitude = (longitude[0]+longitude[1])/2;
		 * 
		 * st.execute("drop extension if exists postgis;");
		 * st.execute("create extension postgis;"); /*if(checkCategory) {
		 * if(!checkCategory(categoryID)) { throw new InputMismatchException("없는 카테고리");
		 * }
		 * 
		 * ret = st.executeQuery("select placename, number, address, count, star\r\n" +
		 * "from(\r\n" +
		 * "select placeid,categoryid,category,placename,number,address,count,star \r\n"
		 * +
		 * "from place natural join category natural join city natural join (select customerid,placeid,count(star), round(avg(star)::numeric, 1) as star\r\n"
		 * + "from star\r\n" + "group by rollup(placeid,customerid))as S\r\n" +
		 * "where customerid is null and ST_DistanceSphere(\r\n" +
		 * "    	ST_GeomFromText('POINT("+centerLongitude+" "
		 * +centerLatitude+")', 4326),\r\n" +
		 * "        ST_MakePoint(longitude, latitude, 4326)\r\n" +
		 * "    )/1000 <= (ST_DistanceSphere(ST_MakePoint("+longitude[0]+","+latitude[0]
		 * +",4326), ST_MakePoint("+longitude[1]+","+latitude[1]+",4326))/2000)"+"\r\n"
		 * + "union\r\n" +
		 * "select placeid,categoryid,category,placename,number,address,0,0\r\n" +
		 * "from place natural join category natural join city\r\n" +
		 * "where placeid not in(select placeid from star) and ST_DistanceSphere(\r\n" +
		 * "    	ST_GeomFromText('POINT("+centerLongitude+" "
		 * +centerLatitude+")', 4326),\r\n" +
		 * "        ST_MakePoint(longitude, latitude, 4326)\r\n" +
		 * "    )/1000 <= (ST_DistanceSphere(ST_MakePoint("+longitude[0]+","+latitude[0]
		 * +",4326), ST_MakePoint("+longitude[1]+","+latitude[1]+",4326))/2000)\r\n" +
		 * ") as S natural join placeLocation where categoryid="+categoryID+";"); } else
		 * { ret =
		 * st.executeQuery("select category, placename, number, address,count, star\r\n"
		 * + "from(\r\n" +
		 * "select placeid,categoryid,category,placename,number,address,count,star \r\n"
		 * +
		 * "from place natural join category natural join city natural join (select customerid,placeid,count(star), round(avg(star)::numeric, 1) as star\r\n"
		 * + "from star\r\n" + "group by rollup(placeid,customerid))as S\r\n" +
		 * "where customerid is null and ST_DistanceSphere(\r\n" +
		 * "    	ST_GeomFromText('POINT("+centerLongitude+" "
		 * +centerLatitude+")', 4326),\r\n" +
		 * "        ST_MakePoint(longitude, latitude, 4326)\r\n" +
		 * "    )/1000 <= (ST_DistanceSphere(ST_MakePoint("+longitude[0]+","+latitude[0]
		 * +",4326), ST_MakePoint("+longitude[1]+","+latitude[1]+",4326))/2000)"+"\r\n"
		 * + "union\r\n" +
		 * "select placeid,categoryid,category,placename,number,address,0,0\r\n" +
		 * "from place natural join category natural join city\r\n" +
		 * "where placeid not in(select placeid from star) and ST_DistanceSphere(\r\n" +
		 * "    	ST_GeomFromText('POINT("+centerLongitude+" "
		 * +centerLatitude+")', 4326),\r\n" +
		 * "        ST_MakePoint(longitude, latitude, 4326)\r\n" +
		 * "    )/1000 <= (ST_DistanceSphere(ST_MakePoint("+longitude[0]+","+latitude[0]
		 * +",4326), ST_MakePoint("+longitude[1]+","+latitude[1]+",4326))/2000)\r\n" +
		 * ") as S natural join placeLocation;"); //}
		 * 
		 * retMeta = ret.getMetaData(); printTable(ret,retMeta);
		 * 
		 * } catch(SQLException ex) { System.out.println(ex); }
		 * catch(InputMismatchException ex) { System.out.println(ex); }
		 */

	}

	public void updateStar(String city, String place, int star) {
		ResultSet ret = null;
		ResultSetMetaData retMeta = null;
		int cityID = 0;
		int placeID = 0;
		try {
			if (star < 0 || star > 5) {
				throw new InputMismatchException("1~5점 사이 입력!");
			}
			ret = st.executeQuery("select count(*) from city where city='" + city + "';");
			while (ret.next()) {
				int tmp = Integer.parseInt(ret.getString(1));
				if (tmp == 0) {
					throw new InputMismatchException("없는 도시.");
				}
				ret = st.executeQuery("select cityID from city where city='" + city + "';");
				ret.next();
				cityID = Integer.parseInt(ret.getString(1));
				break;
			}
			ret = st.executeQuery(
					"select count(*) from place where placename='" + place + "' and cityid=" + cityID + ";");
			while (ret.next()) {
				int tmp = Integer.parseInt(ret.getString(1));
				if (tmp == 0) {
					throw new InputMismatchException("없는 관광지");
				}

				ret = st.executeQuery(
						"select placeID from place where placename='" + place + "' and cityid=" + cityID + ";");
				ret.next();
				placeID = Integer.parseInt(ret.getString(1));
				break;
			}
			st.execute("delete from star where customerid is null and star =0 and placeid=" + placeID + ";");
			st.execute("insert into star values('" + user.getUserID() + "'," + placeID + "," + star + ");");
			ret = st.executeQuery(
					"select placename, count, star from place natural join (select customerid,placeid, count(star), round(avg(star)::numeric, 1) as star\r\n"
							+ "from star\r\n" + "group by rollup(placeid,customerid)) as S"
							+ " where customerid is null and placeid=" + placeID + ";");

			retMeta = ret.getMetaData();

			printTable(ret, retMeta);
		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (InputMismatchException ex) {
			System.out.println(ex);
		}

	}

	public void printTable(ResultSet ret, ResultSetMetaData retMeta) throws SQLException {
		int count = 0;
		int j = 1;
		String[] columns = null;
		count = retMeta.getColumnCount();
		columns = new String[count];
		System.out.format("     ");
		for (int i = 0; i < count; i++) {
			columns[i] = retMeta.getColumnLabel(i + 1);
			System.out.format("%-60s", columns[i]);
		}
		System.out.println("");
		while (ret.next()) {
			System.out.format("%-5s", j++);
			for (int i = 0; i < count; i++) {
				System.out.format("%-60s", ret.getString(i + 1));
			}
			System.out.println("");
		}
	}

	public void printCategory() {
		ResultSet ret = null;
		ResultSetMetaData retMeta = null;

		try {
			ret = st.executeQuery("select * from category;");

			while (ret.next()) {
				System.out.format("%s.%s\n", ret.getString(1), ret.getString(2));
			}

		} catch (SQLException ex) {
			System.out.println(ex);
		}

	}

	public void startService() {
		int flag = 1;
		int categoryID = 0;
		String city = null;
		int orderbyStar = 0;
		int checkCategory = 0;
		int coursePlaceNum = 0;
		ArrayList<courseCriteriaObj> courseCriteria = new ArrayList<courseCriteriaObj>();
		String srcCity = null, dstCity = null;
		String placeName = null;
		int star = 0;
		Scanner scan = new Scanner(System.in);

		while (true) {
			while (true) {
				System.out.format(
						"1.카테고리별 관광지 조회\n2.도시별 관광지 조회\n3.카테고리 및 도시별 관광지 조회\n4.두 도시 이동 간의 추천 관광지\n5.후기 남기기\n0.종료\n");
				try {
					System.out.format("입력 : ");
					flag = scan.nextInt();
					scan.nextLine();
					if (flag < 0 || flag > 5) {
						throw new InputMismatchException("올바른 값을 입력해주세요.");
					}
					break;
				} catch (InputMismatchException ex) {
					System.out.println(ex);
				}
			}

			switch (flag) {
			case 1:
				printCategory();
				System.out.format("카테고리 입력 : ");
				categoryID = scan.nextInt();
				scan.nextLine();
				while (true) {
					try {
						System.out.format("\n정렬기준\n 1.별점순 2.기본순\n");
						System.out.format("정렬 기준 입력 : ");
						orderbyStar = scan.nextInt();
						scan.nextLine();
						if (orderbyStar != 1 && orderbyStar != 2) {
							throw new InputMismatchException("올바른 값을 입력해주세요.");
						}
						break;
					} catch (InputMismatchException ex) {
						System.out.println(ex);
					}
				}
				System.out.println("");
				if (orderbyStar == 1) {
					readCategoryPlace(categoryID, true);
				} else {
					readCategoryPlace(categoryID, false);
				}
				break;
			case 2:
				System.out.format("도시 입력 (ex 수원시, 가평군) : ");
				city = scan.nextLine();
				while (true) {
					try {
						System.out.format("\n정렬기준\n 1.별점순 2.기본순\n");
						System.out.format("정렬 기준 입력 : ");
						orderbyStar = scan.nextInt();
						scan.nextLine();
						if (flag != 1 && flag != 2) {
							throw new InputMismatchException("올바른 값을 입력해주세요.");
						}
						break;
					} catch (InputMismatchException ex) {
						System.out.println(ex);
					}
				}
				if (orderbyStar == 1) {
					readCityPlace(city, true);
				} else {
					readCityPlace(city, false);
				}
				break;
			case 3:
				System.out.format("도시 입력 (ex 수원시, 가평군): ");
				city = scan.nextLine();
				printCategory();
				System.out.format("\n카테고리 입력 : ");
				categoryID = scan.nextInt();
				scan.nextLine();
				while (true) {
					try {
						System.out.format("\n정렬기준\n 1.별점순 2.기본순\n");
						System.out.format("정렬 기준 입력 : ");
						orderbyStar = scan.nextInt();
						scan.nextLine();
						if (orderbyStar != 1 && orderbyStar != 2) {
							throw new InputMismatchException("올바른 값을 입력해주세요.");
						}
						break;
					} catch (InputMismatchException ex) {
						System.out.println(ex);
					}
				}
				if (orderbyStar == 1) {
					readCategoryCityPlace(city, categoryID, true);
				} else {
					readCategoryCityPlace(city, categoryID, false);
				}
				break;
			case 4:
				System.out.format("출발 도시 : ");
				srcCity = scan.nextLine();
				System.out.format("도착도시 : ");
				dstCity = scan.nextLine();
				int categoryId;
				int eachPlaceNum;
				int totalPlaceNum = 0;
				// while(true) {
				// try {
				System.out.format("\n코스 내 여행지 개수 : \n");
				coursePlaceNum = scan.nextInt();
				printCategory();

				while (true) {
					for(int i=0;i<coursePlaceNum;i++) {
						System.out.printf("<%d>\n",i+1);
						try {
							System.out.format("카테고리ID : ");
							categoryId = scan.nextInt();
							if (!checkCategory(categoryID)) {
								throw new InputMismatchException("없는 카테고리");
							}

							System.out.format("여행지 개수 :");
							eachPlaceNum = scan.nextInt();
							totalPlaceNum+=eachPlaceNum;
							courseCriteria.add(new courseCriteriaObj(categoryId, eachPlaceNum));
						}
						catch (SQLException ex) {
							System.out.println(ex);
						}
					}
					if(totalPlaceNum!=coursePlaceNum) {
						System.out.println("입력하신 여행지 개수가 사전에 입력한 수보다 적거나 많습니다. 다시 입력해주세요.");
						courseCriteria.clear();
						totalPlaceNum=0;
					}
					else {
						readCoursePlace(srcCity, dstCity, coursePlaceNum, courseCriteria);
						courseCriteria.clear();
						break;
					}
				}

					
				
				/*
				 * System.out.format("정렬 기준 입력 : "); checkCategory = scan.nextInt();
				 * scan.nextLine(); if(checkCategory!=1 && checkCategory!=2) { throw new
				 * InputMismatchException("올바른 값을 입력해주세요."); } break;
				 */
				// }
				// catch(InputMismatchException ex){
				// System.out.println(ex);
				// }
				// }
				/*
				 * if(checkCategory == 1) { System.out.println(""); printCategory();
				 * System.out.format("카테고리 입력 : "); categoryID = scan.nextInt();
				 * scan.nextLine(); readCoursePlace(srcCity, dstCity,true, categoryID); } else {
				 */
				readCoursePlace(srcCity, dstCity, coursePlaceNum, courseCriteria);
				// }
				break;
			case 5:
				System.out.format("관광지 명 : ");
				placeName = scan.nextLine();
				System.out.format("도시 명: ");
				city = scan.nextLine();
				System.out.format("별점(1~5) : ");
				star = scan.nextInt();
				scan.nextLine();

				updateStar(city, placeName, star);

				break;
			case 0:
				System.out.println("이용해주셔서 감사합니다.");
				return;
			}
		}
	}

}
