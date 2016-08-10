package com.cooksys.serialization.assignment;

import com.cooksys.serialization.assignment.model.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

	/**
	 * Creates a {@link Student} object using the given studentContactFile. The
	 * studentContactFile should be an XML file containing the marshaled form of
	 * a {@link Contact} object.
	 *
	 * @param studentContactFile
	 *            the XML file to use
	 * @param jaxb
	 *            the JAXB context to use
	 * @return a {@link Student} object built using the {@link Contact} data in
	 *         the given file
	 * @throws JAXBException
	 */
	public static Student readStudent(File studentContactFile, JAXBContext jaxb) throws JAXBException {
		Unmarshaller unmarshaller = jaxb.createUnmarshaller();
		Contact contact = (Contact) unmarshaller.unmarshal(studentContactFile);
		Student student = new Student();
		student.setContact(contact);
		return student;

	}

	/**
	 * Creates a list of {@link Student} objects using the given directory of
	 * student contact files.
	 *
	 * @param studentDirectory
	 *            the directory of student contact files to use
	 * @param jaxb
	 *            the JAXB context to use
	 * @return a list of {@link Student} objects built using the contact files
	 *         in the given directory
	 * @throws JAXBException
	 */
	public static List<Student> readStudents(File studentDirectory, JAXBContext jaxb) throws JAXBException {
		List<Student> list = new ArrayList<Student>();
		File[] files = studentDirectory.listFiles();
		if (files != null) {
			for (File file : files) {
				list.add(readStudent(file, jaxb));
			}
		}
		return list;

	}

	/**
	 * Creates an {@link Instructor} object using the given
	 * instructorContactFile. The instructorContactFile should be an XML file
	 * containing the marshaled form of a {@link Contact} object.
	 *
	 * @param instructorContactFile
	 *            the XML file to use
	 * @param jaxb
	 *            the JAXB context to use
	 * @return an {@link Instructor} object built using the {@link Contact} data
	 *         in the given file
	 */
	public static Instructor readInstructor(File instructorContactFile, JAXBContext jaxb) {
		Unmarshaller unmarshaller;
		Contact contact = null;
		try {
			unmarshaller = jaxb.createUnmarshaller();
			contact = (Contact) unmarshaller.unmarshal(instructorContactFile);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Instructor inst = new Instructor();
		inst.setContact(contact);
		return inst;
	}

	/**
	 * Creates a {@link Session} object using the given rootDirectory. A
	 * {@link Session} root directory is named after the location of the
	 * {@link Session}, and contains a directory named after the start date of
	 * the {@link Session}. The start date directory in turn contains a
	 * directory named `students`, which contains contact files for the students
	 * in the session. The start date directory also contains an instructor
	 * contact file named `instructor.xml`.
	 *
	 * @param rootDirectory
	 *            the root directory of the session data, named after the
	 *            session location
	 * @param jaxb
	 *            the JAXB context to use
	 * @return a {@link Session} object built from the data in the given
	 *         directory
	 */
	public static Session readSession(File rootDirectory, JAXBContext jaxb) {
		File[] datefile = rootDirectory.listFiles();
		int dateIndex = 0;
		// find index of date folder
		for (int i = 0; i < datefile.length; i++) {
			if (datefile[i].isDirectory()) {
				dateIndex = i;
				break;
			}

		}
		File[] stuDir = datefile[dateIndex].listFiles();
		List<Student> students = null;
		// find index of folder
		int studentFolderIndex = 0;
		int instFileIndex = 0;
		for (int i = 0; i < stuDir.length; i++) {
			//System.out.println(stuDir[i].getName() + " is dir: " + stuDir[i].isDirectory());
			if (stuDir[i].isDirectory()) {
				studentFolderIndex = i;

			}
			if (stuDir[i].isFile()) {
				instFileIndex = i;

			}

		}
		//System.out.println(studentFolderIndex);
		try {
			students = readStudents(stuDir[studentFolderIndex], jaxb);
			// System.out.println(students..getContact().getFirstName());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Instructor instructor = new Instructor();
		String instdir = rootDirectory.listFiles()[dateIndex].listFiles()[instFileIndex].toString();
		File instFile = new File(instdir);
		instructor = readInstructor(instFile, jaxb);

		Session session = new Session();
		session.setStudents(students);
		session.setInstructor(instructor);
		String location = rootDirectory.getName();
		String date = rootDirectory.listFiles()[0].getName();
		session.setLocation(location);
		session.setStartDate(date);
		return session;

	}

	/**
	 * Writes a given session to a given XML file
	 *
	 * @param session
	 *            the session to write to the given file
	 * @param sessionFile
	 *            the file to which the session is to be written
	 * @param jaxb
	 *            the JAXB context to use
	 */
	public static void writeSession(Session session, File sessionFile, JAXBContext jaxb) {
		Marshaller marshaller = null;
		try {
			marshaller = jaxb.createMarshaller();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			marshaller.marshal(session, sessionFile);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Main Method Execution Steps: 1. Configure JAXB for the classes in the
	 * com.cooksys.serialization.assignment.model package 2. Read a session
	 * object from the <project-root>/input/memphis/ directory using the methods
	 * defined above 3. Write the session object to the
	 * <project-root>/output/session.xml file.
	 *
	 * JAXB Annotations and Configuration: You will have to add JAXB annotations
	 * to the classes in the com.cooksys.serialization.assignment.model package
	 *
	 * Check the XML files in the <project-root>/input/ directory to determine
	 * how to configure the {@link Contact} JAXB annotations
	 *
	 * The {@link Session} object should marshal to look like the following:
	 * <session location="..." start-date="..."> <instructor> <contact>...
	 * </contact> </instructor> <students> ...
	 * <student> <contact>...</contact> </student> ... </students> </session>
	 */
	public static void main(String[] args) {
		Session session = new Session();
		JAXBContext jaxb = null;
		try {
			jaxb = JAXBContext.newInstance(Session.class, Student.class, Instructor.class);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		session = readSession(new File("input/memphis/"), jaxb);
		writeSession(session, new File("output/session.xml"), jaxb);
	}
}
