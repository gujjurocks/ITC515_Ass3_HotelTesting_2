package hotel.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hotel.credit.CreditCard;
import hotel.utils.IOUtils;

public class Hotel {
	
	private Map<Integer, Guest> guests;
	public Map<RoomType, Map<Integer,Room>> roomsByType;
	public Map<Long, Booking> bookingsByConfirmationNumber;
	public Map<Integer, Booking> activeBookingsByRoomId;
	
	
	public Hotel() {
		guests = new HashMap<>();
		roomsByType = new HashMap<>();
		for (RoomType rt : RoomType.values()) {
			Map<Integer, Room> rooms = new HashMap<>();
			roomsByType.put(rt, rooms);
		}
		bookingsByConfirmationNumber = new HashMap<>();
		activeBookingsByRoomId = new HashMap<>();
	}

	
	public void addRoom(RoomType roomType, int id) {
		IOUtils.trace("Hotel: addRoom");
		for (Map<Integer, Room> rooms : roomsByType.values()) {
			if (rooms.containsKey(id)) {
				throw new RuntimeException("Hotel: addRoom : room number already exists");
			}
		}
		Map<Integer, Room> rooms = roomsByType.get(roomType);
		Room room = new Room(id, roomType);
		rooms.put(id, room);
	}

	
	public boolean isRegistered(int phoneNumber) {
		return guests.containsKey(phoneNumber);
	}

	
	public Guest registerGuest(String name, String address, int phoneNumber) {
		if (guests.containsKey(phoneNumber)) {
			throw new RuntimeException("Phone number already registered");
		}
		Guest guest = new Guest(name, address, phoneNumber);
		guests.put(phoneNumber, guest);		
		return guest;
	}

	
	public Guest findGuestByPhoneNumber(int phoneNumber) {
		Guest guest = guests.get(phoneNumber);
		return guest;
	}

	
	public Booking findActiveBookingByRoomId(int roomId) {
		Booking booking = activeBookingsByRoomId.get(roomId);;
		return booking;
	}


	public Room findAvailableRoom(RoomType selectedRoomType, Date arrivalDate, int stayLength) {
		IOUtils.trace("Hotel: checkRoomAvailability");
		Map<Integer, Room> rooms = roomsByType.get(selectedRoomType);
		for (Room room : rooms.values()) {
			IOUtils.trace(String.format("Hotel: checking room: %d",room.getId()));
			if (room.isAvailable(arrivalDate, stayLength)) {
				return room;
			}			
		}
		return null;
	}

	
	public Booking findBookingByConfirmationNumber(long confirmationNumber) {
		return bookingsByConfirmationNumber.get(confirmationNumber);
	}

	
	public long book(Room room, Guest guest, 
			Date arrivalDate, int stayLength, int occupantNumber,
			CreditCard creditCard) {
		
		// Book room using given information i.e room, guest, stayLength, occupantNumber, creaditCard
		Booking bookedRoomInformation = room.book();
		// confirm booking and get confirmation number
		Long confirmatioNumber = bookedRoomInformation.getConfirmationNumber();
		// add in bookingsByConfirmationNumber
		bookingsByConfirmationNumber.put(confirmatioNumber, bookedRoomInformation);
		
		// create unique confirmation number
		String roomId = String.valueOf(room.getRoomId());
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
		String bookingDate = formatter.format(date);
		// unique booking ID
		long bookingId = Long.valueOf(bookingDate + roomId);
		
		// Room should not be available for this dates
			// Mark this Room not available for date arrivalDate + stayLength
			
		
		return bookingId;		
	}

	
	public void checkin(long confirmationNumber) {
		// check if booking exist or not
		if (currentBooking != null) {
			currentBooking.getRoomID();
			currentBooking.checkin(); // change status of booking to CHECKED_IN
			activeBookingsByRoomId.put(confirmationNumber,currentBooking);
		} else {
			throw new RuntimeException("Booking with confirmation number : " + confirmationNumber + " does not exist.");
		}	}


	public void addServiceCharge(int roomId, ServiceType serviceType, double cost) {
		Booking currentBooking = findActiveBookingByRoomId(roomId);
		if (currentBooking != null) {
			currentBooking.addServiceCharge(cost); // Use serviceType logic here if available 
		} else {
			throw new RuntimeException("Booking with Room id : " + roomId + " does not exist.");
		}
	}

	
	public void checkout(int roomId) {
		// TODO Auto-generated method stub
	}


}
