package com.example.demo.login.dmain.repository.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import com.example.demo.login.dmain.model.User;
import com.example.demo.login.dmain.repository.UserDao;


@Repository("UserDaoJdbcImpl")
public class UserDaoJdbcImpl implements UserDao {
	
	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	// Userテーブルの件数を取得
	@Override
	public int count() throws DataAccessException{
		
		//　全件取得してカウント
		int count = jdbc.queryForObject("SELECT COUNT(*) FROM m_user",Integer.class);
		
		return count;
	}
	
	// Userテーブルにデータを1件insert.
	@Override
	public int insertOne(User user) throws DataAccessException{
		
		//パスワード暗号化
		String password = passwordEncoder.encode(user.getPassword());
		
		//1件登録
		String sql = "INSERT INTO m_user("
				+ "user_id,"
				+ "password,"
				+ "user_name,"
				+ "birthday,"
				+ "age,"
				+ "marriage,"
				+ "role)"
				+ "VALUES(?,?,?,?,?,?,?)";
		
		// 1件挿入
		int rowNumber = jdbc.update(sql,
				 user.getUserId(),
				 password,
				 user.getUserName(),
				 user.getBirthday(),
				 user.getAge(),
				 user.isMarriage(),
				 user.getRole());
		
		return rowNumber;
	}
	
	// Userテーブルを1件取得
	@Override
	public User selectOne(String userId) throws DataAccessException {
		
		// 1件取得
		Map<String, Object> map = jdbc.queryForMap("SELECT * FROM m_user"
				+ " WHERE user_id =? "
				,userId);
		
		// 結果返却用の変数
		User user = new User();
		
		// 取得したデータを結果返却用の変数にセットしていく
		user.setUserId((String)map.get("user_id")); // ユーザーID
		user.setPassword((String)map.get("password")); // パスワード
		user.setUserName((String)map.get("user_name")); // ユーザ名
		user.setBirthday((Date)map.get("birthday")); // 誕生日
		user.setAge((Integer)map.get("age")); // 年齢
		user.setMarriage((Boolean)map.get("marriage")); // 結婚ステータス
		user.setRole((String)map.get("role")); // ロール
		
		return user;
	}
	
	// Userテーブルの全データを1件取得
	@Override
	public List<User> selectMany() throws DataAccessException {
		
		// M_USERテーブルのデータを全件削除
		List<Map<String, Object>> getList = jdbc.queryForList("SELECT * FROM m_user");
		
		// 結果返却用の変数
		List<User> userList = new ArrayList<>();
		
		// 取得したデータを結果編訳用のListに格納指定く
		for(Map<String, Object> map: getList) {
			
			//Userインスタンスの生成
			User user = new User();
			
			// Userインスタンスに取得したデータをセットする
			user.setUserId((String)map.get("user_id")); // ユーザーID
			user.setPassword((String)map.get("password")); // パスワード
			user.setUserName((String)map.get("user_name")); // ユーザ名
			user.setBirthday((Date)map.get("birthday")); // 誕生日
			user.setAge((Integer)map.get("age")); // 年齢
			user.setMarriage((Boolean)map.get("marriage")); // 結婚ステータス
			user.setRole((String)map.get("role")); // ロール
			
			// 結果返却用のListに追加
			userList.add(user);
		}
		return userList;
	}
	
	// Userテーブルを1件更新
	@Override
	public int updateOne(User user) throws DataAccessException {
		
		// パスワード暗号化
		String password = passwordEncoder.encode(user.getPassword());
		
		// 1件更新
		String sql = "UPDATE m_user SET"
				+ " password = ?,"
				+ " user_name = ?,"
				+ " birthday = ?,"
				+ " age = ?,"
				+ " marriage = ?"
				+ " WHERE user_id = ?";
		
		// 1件挿入
		int rowNumber = jdbc.update(sql,
				 password,
				 user.getUserName(),
				 user.getBirthday(),
				 user.getAge(),
				 user.isMarriage(),
				 user.getUserId());
		
		// トラザクション確認のため、わざと例外をthrowする
		if (rowNumber > 0) {
			throw new DataAccessException("トラザクションテスト") {};
		}
		
		return rowNumber;
	}
	
	// Userテーブルを1件削除
	@Override
	public int deleteOne(String userId) throws DataAccessException {
		
		// 1件削除
		int rowNumber = jdbc.update("DELETE FROM m_user WHERE user_id = ?", userId);
		
		return rowNumber;
	}
	
	// SQLの習得結果をサーバーでCSVで保存する
	@Override
	public void userCsvOut() throws DataAccessException {
		
		// M_USERテーブルのデータを全件取得するSQL
		String sql = "SELECT * FROM m_user";
		
		// ResultSetExtactorの生成
		UserRowCallbackHandler handler = new UserRowCallbackHandler();
		
		// SQL実行&CSV出力
		jdbc.query(sql, handler);
	}
}