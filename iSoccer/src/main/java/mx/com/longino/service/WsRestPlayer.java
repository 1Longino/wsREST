package mx.com.longino.service;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.ws.tpv.engine.exceptions.NotFoundException;

import mx.com.longino.config.XGCalConverter;
import mx.com.longino.connection.BdSoccer;
import mx.com.longino.entity.Player;
import mx.com.longino.util.DateHandlerDeserializable;

@Path("/jugadores")
public class WsRestPlayer {

	Connection oCon = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	private static final String SQL_PLAYER = "select CURP,NAME,LAST_NAME,SECOND_NAME,BIRTHDAY,POSITION,DIRECTION,GENDER,PHONE,EMAIL,UPDATED_BY,UPDATED_WHEN,REGION from player where CURP = ?";
	private static final String INSERT_PLAYER = "insert into PLAYER (CURP,NAME,LAST_NAME,SECOND_NAME,BIRTHDAY,POSITION,DIRECTION,GENDER,PHONE,EMAIL,UPDATED_BY,UPDATED_WHEN,REGION) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_All_PLAYER = "select CURP,NAME,LAST_NAME,SECOND_NAME,BIRTHDAY,POSITION,DIRECTION,GENDER,PHONE,EMAIL,UPDATED_BY,UPDATED_WHEN,REGION from player";
	private static final String DML_UPDATE_PLAYER = "update player set NAME = ?,LAST_NAME = ?,SECOND_NAME = ?,BIRTHDAY = ?,POSITION = ?,DIRECTION = ?,GENDER = ?,PHONE = ?,EMAIL = ?,UPDATED_BY = ?,UPDATED_WHEN = sysdate ,REGION = ? where curp = ?";

	XMLGregorianCalendar xmlGregorianCalendarDate = null;
	GregorianCalendar gregorianCalendar = null;

	public WsRestPlayer() {

	}

	@GET
	@Path("/consultaJugador/{id}/{region}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Player getPlayer(@PathParam("id") String ls_id,
			@PathParam("region") int li_region) {
		Player player = new Player();
		try {
			oCon = BdSoccer.getConnectionDB(li_region);
			preparedStatement = oCon.prepareStatement(SQL_PLAYER);
			preparedStatement.setString(1, ls_id);
			resultSet = preparedStatement.executeQuery();

			if (resultSet != null) {

				while (resultSet.next()) {

					player.setCurp(resultSet.getString("CURP"));
					player.setName(resultSet.getString("NAME"));
					player.setLastName(resultSet.getString("LAST_NAME"));
					player.setSecondName(resultSet.getString("SECOND_NAME"));

					System.out
							.println("**************Converting type Date to XmlGregorianCalendar***********");
					gregorianCalendar = new GregorianCalendar();
					gregorianCalendar.setTime(resultSet.getDate("BIRTHDAY"));

					// try {
					// xmlGregorianCalendarDate =
					// DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
					// } catch (DatatypeConfigurationException e) {
					// // TODO Bloque catch generado automáticamente
					// e.printStackTrace();
					// }
					// player.setBirthday(xmlGregorianCalendarDate);

					player.setBirthday(resultSet.getDate("BIRTHDAY").toString());

					player.setPosition(resultSet.getString("POSITION"));
					player.setDirection(resultSet.getString("DIRECTION"));
					player.setGender(resultSet.getString("GENDER"));
					player.setPhone(resultSet.getString("PHONE"));
					player.setEmail(resultSet.getString("EMAIL"));
					player.setUpatedBy(resultSet.getString("UPDATED_BY"));

					gregorianCalendar = new GregorianCalendar();
					gregorianCalendar
							.setTime(resultSet.getDate("UPDATED_WHEN"));

					// try {
					// xmlGregorianCalendarDate =
					// DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
					// } catch (DatatypeConfigurationException e) {
					// // TODO Bloque catch generado automáticamente
					// e.printStackTrace();
					// }
					// player.setUpdatedWhen(xmlGregorianCalendarDate);
					player.setUpdatedWhen(resultSet.getDate("UPDATED_WHEN")
							.toString());

					player.setRegion(resultSet.getInt("REGION"));
					BdSoccer.closeDB(preparedStatement);
					BdSoccer.closeDB(resultSet);
					BdSoccer.returnConnectionDB(oCon);
				}
			} else {
				// throw new WebApplicationException(Response.Status.NOT_FOUND);
				throw new WebApplicationException(Response.Status.FORBIDDEN);
				// throw new NotFoundException();
			}

		} catch (SQLException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
			BdSoccer.returnConnectionDB(oCon);
		} catch (NamingException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
			BdSoccer.returnConnectionDB(oCon);
		} finally {
			BdSoccer.closeDB(preparedStatement);
			BdSoccer.closeDB(resultSet);
			BdSoccer.returnConnectionDB(oCon);
		}

		return player;
	}

	@GET
	@Path("/consultaJugadores/{region}")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Player> getPlayers(@PathParam("region") int li_region) {

		List<Player> ll_players = new ArrayList<Player>();
		Player player = null;
		try {
			oCon = BdSoccer.getConnectionDB(li_region);
			preparedStatement = oCon.prepareStatement(SQL_All_PLAYER);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String curp = resultSet.getString("CURP");
				String name = resultSet.getString("NAME");
				String lastName = resultSet.getString("LAST_NAME");
				String secondName = resultSet.getString("SECOND_NAME");

				System.out
						.println("**************Converting type Date to XmlGregorianCalendar***********");
				// gregorianCalendar = new GregorianCalendar();
				// gregorianCalendar.setTime(resultSet.getDate("BIRTHDAY"));

				// XMLGregorianCalendar birthday = null;
				// try {
				// birthday = xmlGregorianCalendarDate =
				// DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
				// } catch (DatatypeConfigurationException e) {
				// // TODO Bloque catch generado automáticamente
				// e.printStackTrace();
				// }
				// System.out.println("**************Converting type Date to XmlGregorianCalendar***********");
				String birthday = resultSet.getDate("BIRTHDAY").toString();

				String position = resultSet.getString("POSITION");
				String direction = resultSet.getString("DIRECTION");
				String gender = resultSet.getString("GENDER");
				String phone = resultSet.getString("PHONE");
				String email = resultSet.getString("EMAIL");
				String upatedBy = resultSet.getString("UPDATED_BY");

				// System.out.println("**************Converting type Date to XmlGregorianCalendar***********");
				// gregorianCalendar = new GregorianCalendar();
				// gregorianCalendar.setTime(resultSet.getDate("UPDATED_WHEN"));
				//
				// XMLGregorianCalendar updatedWhen = null ;
				// try {
				// updatedWhen = xmlGregorianCalendarDate =
				// DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
				// } catch (DatatypeConfigurationException e) {
				// // TODO Bloque catch generado automáticamente
				// e.printStackTrace();
				// }
				// System.out.println("**************Converting type Date to XmlGregorianCalendar***********");

				String updatedWhen = resultSet.getDate("UPDATED_WHEN")
						.toString();
				int region = resultSet.getInt("REGION");

				player = new Player(curp, name, lastName, secondName, birthday,
						position, direction, gender, phone, email, upatedBy,
						updatedWhen, region);

				ll_players.add(player);
			}
		} catch (SQLException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}

		return ll_players;
	}

	@POST
	@Path("/creaJugador")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createPlayer(String ls_jsonCreatePlayerIn) {

		int registrosInsertados = 0;
		Player createPlayerJsonReq = null;

		try {

			System.out
					.println("************************BEFORE JSON BUILDER********************************");

			final String FORMATO_FECHA = "yyyy-MM-dd HH:mm:ss";
			final DateFormat DF = new SimpleDateFormat(FORMATO_FECHA);
			final Gson gson = new GsonBuilder().setDateFormat(FORMATO_FECHA)
					.create();
			// Gson gson = new
			// GsonBuilder().registerTypeAdapter(XMLGregorianCalendar.class, new
			// XGCalConverter.Serializer()).registerTypeAdapter(XMLGregorianCalendar.class,
			// new XGCalConverter.Deserializer()).create();

			System.out
					.println("************************AFTER JSON BUILDER********************************");

			createPlayerJsonReq = gson.fromJson(ls_jsonCreatePlayerIn,
					Player.class);

			oCon = BdSoccer.getConnectionDB(createPlayerJsonReq.getRegion());
			preparedStatement = oCon.prepareStatement(INSERT_PLAYER);
			preparedStatement.setString(1, createPlayerJsonReq.getCurp());
			preparedStatement.setString(2, createPlayerJsonReq.getName());
			preparedStatement.setString(3, createPlayerJsonReq.getLastName());
			preparedStatement.setString(4, createPlayerJsonReq.getSecondName());

			SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_FECHA);
			String strDate = createPlayerJsonReq.getBirthday();
			Date date = sdf.parse(strDate);
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());

			preparedStatement.setDate(5, sqlDate);
			preparedStatement.setString(6, createPlayerJsonReq.getPosition());
			preparedStatement.setString(7, createPlayerJsonReq.getDirection());
			preparedStatement.setString(8, createPlayerJsonReq.getGender());
			preparedStatement.setString(9, createPlayerJsonReq.getPhone());
			preparedStatement.setString(10, createPlayerJsonReq.getEmail());
			preparedStatement.setString(11, createPlayerJsonReq.getUpatedBy());

			String strUpdateWhen = createPlayerJsonReq.getUpdatedWhen();
			date = sdf.parse(strUpdateWhen);
			java.sql.Date sqlDate2 = new java.sql.Date(date.getTime());
			preparedStatement.setDate(12, sqlDate2);
			preparedStatement.setInt(13, createPlayerJsonReq.getRegion());
			registrosInsertados = preparedStatement.executeUpdate();
			oCon.commit();
			BdSoccer.closeDB(preparedStatement);
			BdSoccer.returnConnectionDB(oCon);
			System.out.println(registrosInsertados
					+ "== Registro Insertado con Éxito");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.out);
			BdSoccer.returnConnectionDB(oCon);
			return Response.notModified().build();

		} catch (NamingException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
			BdSoccer.returnConnectionDB(oCon);
			return Response.notModified().build();
		} catch (ParseException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}

		finally {
			BdSoccer.closeDB(preparedStatement);
			BdSoccer.returnConnectionDB(oCon);
		}

		return Response.ok(createPlayerJsonReq).build();

	}

	@PUT
	@Path("/actualizaJugador")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updatePlayer(String ls_jsonModifyPlayerIn) {

		int registrosInsertados = 0;
		Player updatePlayerJsonReq = null;

		System.out.println("*****************JSON in == "
				+ ls_jsonModifyPlayerIn);

		try {

			Connection oCon = null;
			System.out
					.println("************************BEFORE JSON BUILDER********************************");

			final String FORMATO_FECHA = "yyyy-MM-dd HH:mm:ss";
			DateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA);

			final Gson gson = new GsonBuilder().setDateFormat(FORMATO_FECHA)
					.create();
			// Gson gson = new
			// GsonBuilder().registerTypeAdapter(XMLGregorianCalendar.class, new
			// XGCalConverter.Serializer()).registerTypeAdapter(XMLGregorianCalendar.class,
			// new XGCalConverter.Deserializer()).create();
			updatePlayerJsonReq = gson.fromJson(ls_jsonModifyPlayerIn,
					Player.class);

			oCon = BdSoccer.getConnectionDB(updatePlayerJsonReq.getRegion());

			preparedStatement = oCon.prepareStatement(DML_UPDATE_PLAYER);
			preparedStatement.setString(1, updatePlayerJsonReq.getName());
			preparedStatement.setString(2, updatePlayerJsonReq.getLastName());
			preparedStatement.setString(3, updatePlayerJsonReq.getSecondName());

			DateFormat sdf = new SimpleDateFormat(FORMATO_FECHA);
			String ls_birthday = updatePlayerJsonReq.getBirthday();
			Date ld_birthday = sdf.parse(ls_birthday);
			java.sql.Date ld_birthday1 = new java.sql.Date(ld_birthday.getTime());
			System.out.println("****ld_birthday == " + ld_birthday1);
			preparedStatement.setDate(4, ld_birthday1);
			// Timestamp date = (Timestamp)
			// updatePlayerJsonReq.getBirthday().toGregorianCalendar().getTime();

			preparedStatement.setString(5, updatePlayerJsonReq.getPosition());
			preparedStatement.setString(6, updatePlayerJsonReq.getDirection());
			preparedStatement.setString(7, updatePlayerJsonReq.getGender());
			preparedStatement.setString(8, updatePlayerJsonReq.getPhone());
			preparedStatement.setString(9, updatePlayerJsonReq.getEmail());
			preparedStatement.setString(10, updatePlayerJsonReq.getUpatedBy());
			preparedStatement.setInt(11, updatePlayerJsonReq.getRegion());
			preparedStatement.setString(12, updatePlayerJsonReq.getCurp());
			registrosInsertados = preparedStatement.executeUpdate();
			oCon.commit();
			BdSoccer.closeDB(preparedStatement);
			BdSoccer.returnConnectionDB(oCon);
			System.out.println(registrosInsertados
					+ "== Registro Actualizado con Éxito");

			/*
			 * if (currentPlayer!=null) { players.put(player.getId(), player);
			 * response = Response.ok().build(); } else { response =
			 * Response.notModified().build(); }
			 * 
			 * return response;*
			 */

		} catch (SQLException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
			BdSoccer.closeDB(preparedStatement);
			BdSoccer.returnConnectionDB(oCon);
			return Response.notModified().build();
		} catch (NamingException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
			BdSoccer.closeDB(preparedStatement);
			BdSoccer.returnConnectionDB(oCon);
			return Response.notModified().build();
		} catch (ParseException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}

		finally {
			BdSoccer.closeDB(preparedStatement);
			BdSoccer.returnConnectionDB(oCon);
		}

		return Response.ok(updatePlayerJsonReq).build();
	}

	@DELETE
	@Path("/eliminarjugador/{id}")
	public Response deletePlayer(@PathParam("id") Long id) {
		/**
		 * Player player = players.get(id); Response response; if (player!=
		 * null) { players.remove(id); response = Response.ok().build();
		 * 
		 * } else { response = Response.notModified().build(); }
		 * 
		 * return response;
		 */
		return null;
	}
}
