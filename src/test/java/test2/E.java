package test2;

import com.ht.util.Con100HR;

import java.sql.ResultSet;
import java.sql.SQLException;

public class E {

	public void test() {
		String account = "HM0001469";
		String name = "瞿仕军";
		String node = "MiCasing";
		String factory = "B2";

		ResultSet resultSet;
		Con100HR con100hr = new Con100HR();
		resultSet = con100hr.executeQuery("select * from DL_PCBASMT WHERE WO = '000001600872'");

		try {
			while (resultSet.next()) {

			}
		}catch (SQLException exception) {

		}
	}

}
