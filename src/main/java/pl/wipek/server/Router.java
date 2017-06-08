package pl.wipek.server;

import pl.wipek.common.Action;
import pl.wipek.db.*;
import pl.wipek.server.db.*;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Krszysztof Adamczyk on 04.05.2017.
 * Routing actions
 */
class Router {

    private static EntityManager entityManager;

    /**
     * @author Krszysztof Adamczyk
     * managing action writed in received object and executing them
     * @param received object received from socket
     * @return Object depending on the action to execute otherwise null
     */
    static Object manageReceivedObject(Object received) {
        Object result = null;
        entityManager = ClientTask.entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        if(received instanceof UsersNH) {
            result = actionForUsers(received);
        }
        else if(received instanceof AdminsNH) {
            result = actionForAdmins(received);
        }
        else if(received instanceof StudentsNH) {
            result = actionForStudents(received);
        }
        else if(received instanceof SchoolYearsNH) {
            result = actionForSchoolYearsNH(received);
        }
        else if(received instanceof SchoolYears) {
            result = actionForSchoolYears(received);
        }
        else if(received instanceof GradesNH) {
            result = actionForGrades(received);
        }
        else if(received instanceof PartialGradesNH) {
            result = actionForPartialGrades(received);
        }
        else if(received instanceof TeachersNH) {
            result = actionForTeachers(received);
        }
        else if(received instanceof CarriedSubjectsNH) {
            result = actionForCarriedSubject(received);
        }
        else if(received instanceof StudentsClassesNH) {
            result = actionForStudentsClasses(received);
        }
        else if(received instanceof ClassesNH) {
            result = actionForClasses(received);
        }
        else if(received instanceof SemestersNH) {
            result = actionForSemesters(received);
        }
        else if(received instanceof ClassifiedsNH) {
            result = actionForClassifieds(received);
        }
        else if(received instanceof SubstitutesNH) {
            result = actionForSubstitutes(received);
        }
        else if(received instanceof SubjectsNH) {
            result = actionForSubjects(received);
        }
        else if(received instanceof Action) {
            result = actionForAction(received);
        }
        entityManager.clear();
        entityManager.getTransaction().commit();
        return result;
    }

    private static Object actionForUsers(Object received) {
        Object result = null;
        String action = ((UsersNH) received).getAction().getAction();
        Users user = new Users((UsersNH)received);
        UsersNH usersNH = (UsersNH)received;
        List resultList;
        switch(action) {
            case "login":
                result = UserAuth.tryLogIn(user);
                break;
            case "logOut":
                result = UserAuth.tryLogOut(user);
                break;
            case "getStudentToUser":
                resultList = entityManager.createQuery("FROM Students st WHERE st.user = :user", Students.class)
                        .setParameter("user", user).getResultList();
                usersNH.setStudent(!resultList.isEmpty() ? new StudentsNH((Students)resultList.get(0)) : null);
                result = usersNH;
                break;
            case "getTeacherToUser":
                resultList = entityManager.createQuery("FROM Teachers tch WHERE tch.user = :user", Teachers.class)
                        .setParameter("user", user).getResultList();
                usersNH.setTeacher(!resultList.isEmpty() ? new TeachersNH((Teachers)resultList.get(0)) : null);
                result = usersNH;
                break;
            case "getAdminToUser":
                resultList = entityManager.createQuery("FROM Admins ad WHERE ad.user = :user", Admins.class)
                        .setParameter("user", user).getResultList();
                usersNH.setAdmin(!resultList.isEmpty() ? new AdminsNH((Admins)resultList.get(0)) : null);
                result = usersNH;
                break;
            case "saveOrUpdate":
                result = saveOrUpdateUser(user, usersNH);
                break;
            case "remove":
                result = removeUserWithChildren(user);
                break;
        }
        return result;
    }

    private static Boolean removeUserWithChildren(Users user) {
        Boolean result;
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
        result = Boolean.TRUE;
        entityManager.flush();
        entityManager.clear();
        return result;
    }

    private static Object saveOrUpdateUser(Users user, UsersNH usersNH) {
        Object result = null;
        if(user.getIdUser() == 0) {
            entityManager.persist(user);
            entityManager.flush();
            entityManager.refresh(user);
        }else {
            entityManager.merge(user);
        }

        if(usersNH.getTeacher() != null && (usersNH.getTeacher().getAction() == null || !usersNH.getTeacher().getAction().getAction().equals("remove"))) {
            result = manageTeacherInUser(user, usersNH);
        }
        else if(usersNH.getAdmin() != null && (usersNH.getAdmin().getAction() == null || !usersNH.getAdmin().getAction().getAction().equals("remove"))) {
            result = manageAdminInUser(user, usersNH);
        }
        else if(usersNH.getStudent() != null && (usersNH.getStudent().getAction() == null || !usersNH.getStudent().getAction().getAction().equals("remove"))) {
            result = manageStudentInUser(user, usersNH);
        }
        entityManager.clear();
        return result;
    }

    private static Object manageTeacherInUser(Users user, UsersNH usersNH) {
        Object result;
        if(usersNH.getType().equals("")) {
            usersNH = removeTeacherFromUser(usersNH);
        } else {
            if(usersNH.getAdmin() != null) {
                usersNH = removeAdminFromUsers(usersNH);
            }
            else if(usersNH.getStudent() != null) {
                usersNH = removeStudentFromUser(usersNH);
            }
            Teachers teachers = new Teachers();
            teachers.setIdTeacher(usersNH.getTeacher().getIdTeacher());
            teachers.setTitle(usersNH.getTeacher().getTitle());
            teachers.setUser(user);
            entityManager.clear();
            Boolean isEmpty = teachers.getIdTeacher() == 0;
            entityManager.merge(teachers);
            if(isEmpty) {
                entityManager.flush();
            }
            user.setTeacher(teachers);
            usersNH = new UsersNH(user);
            usersNH.setTeacher(new TeachersNH(user.getTeacher()));
        }
        result = usersNH;
        return result;
    }

    private static Object manageAdminInUser(Users user, UsersNH usersNH) {
        Object result;
        if(usersNH.getType().equals("")) {
            usersNH = removeAdminFromUsers(usersNH);
        }else {
            if(usersNH.getTeacher() != null) {
                usersNH = removeTeacherFromUser(usersNH);
            }
            else if(usersNH.getStudent() != null) {
                usersNH = removeStudentFromUser(usersNH);
            }
            Admins admins = new Admins();
            admins.setTitle(usersNH.getAdmin().getTitle());
            admins.setIdAdmin(usersNH.getAdmin().getIdAdmin());
            Boolean isEmpty = admins.getIdAdmin() == 0;
            admins.setUser(user);
            entityManager.clear();
            entityManager.merge(admins);
            if(isEmpty) {
                entityManager.flush();
            }
            user.setAdmin(admins);
            usersNH = new UsersNH(user);
            usersNH.setAdmin(new AdminsNH(user.getAdmin()));
        }
        result = usersNH;
        return result;
    }

    private static Object manageStudentInUser(Users user, UsersNH usersNH) {
        Object result;
        if(usersNH.getType().equals("")) {
            usersNH = removeStudentFromUser(usersNH);
        } else {
            if(usersNH.getTeacher() != null) {
                usersNH = removeTeacherFromUser(usersNH);
            }
            else if(usersNH.getAdmin() != null) {
                usersNH = removeAdminFromUsers(usersNH);
            }
            Students students = new Students();
            students.setIdStudent(usersNH.getStudent().getIdStudent());
            Boolean isEmpty = students.getIdStudent() == 0;
            students.setUser(user);
            entityManager.clear();
            entityManager.merge(students);
            if(isEmpty) {
                entityManager.flush();
            }
            user.setStudent(students);
            usersNH = new UsersNH(user);
            usersNH.setStudent(new StudentsNH(user.getStudent()));
        }
        result = usersNH;
        return result;
    }

    private static UsersNH removeTeacherFromUser(UsersNH usersNH) {
        Teachers teachers = new Teachers(usersNH.getTeacher());
        entityManager.remove(entityManager.find(Teachers.class, teachers.getIdTeacher()));
        entityManager.flush();
        usersNH.setTeacher(null);
        return usersNH;
    }

    private static UsersNH removeAdminFromUsers(UsersNH usersNH) {
        Admins admins = new Admins(usersNH.getAdmin());
        entityManager.remove(entityManager.find(Admins.class, admins.getIdAdmin()));
        entityManager.flush();
        usersNH.setAdmin(null);
        return usersNH;
    }

    private static UsersNH removeStudentFromUser(UsersNH usersNH) {
        Students students = new Students(usersNH.getStudent());
        entityManager.remove(entityManager.find(Students.class, students.getIdStudent()));
        entityManager.flush();
        usersNH.setStudent(null);
        return usersNH;
    }

    private static Object actionForAdmins(Object received) {
        Object result;
        String action = ((AdminsNH) received).getAction().getAction();
        Admins admin = new Admins((AdminsNH)received);
        switch (action) {
            case "getUserToAdmin":
                admin.setUser(admin.getUser());
                break;
            case "getClassifiedsToAdmin":
                admin.setClassifieds(admin.getClassifieds());
                break;
            case "getSubstitutesToAdmin":
                admin.setSubstitutes(admin.getSubstitutes());
                break;
        }
        result = new AdminsNH(admin);
        return result;
    }

    private static Object actionForStudents(Object received) {
        Object result;
        String action = ((StudentsNH) received).getAction().getAction();
        Students student = new Students((StudentsNH)received);
        StudentsNH studentsNH = (StudentsNH)received;
        switch (action) {
            case "getStudentsClassesToStudent":
                studentsNH.setStudentsClasses(getStudentsClassesToStudent(student));
                break;
            case "getGradesToStudent":
                studentsNH.setGrades(getGradesToStudent(student));
                break;
        }
        result = studentsNH;
        return result;
    }

    private static Set<GradesNH> getGradesToStudent(Students student) {
        Set<Grades> grades = new HashSet<>(entityManager.createQuery("FROM Grades gr WHERE gr.student = :student", Grades.class)
                .setParameter("student", student).getResultList());
        Set<GradesNH> gradesNHS = new HashSet<>(0);

        grades.forEach(i -> {
            Set<PartialGrades> partialGrades = i.getPartialGrades();
            Set<PartialGradesNH> partialGradesNHS = new HashSet<>(0);
            partialGrades.forEach(it -> partialGradesNHS.add(new PartialGradesNH(it)));
            GradesNH gradesNH = new GradesNH(i);
            gradesNH.setPartialGrades(partialGradesNHS);
            gradesNHS.add(gradesNH);

        });
        return gradesNHS;
    }

    private static Set<StudentsClassesNH> getStudentsClassesToStudent(Students student) {
        Set<StudentsClasses> studentsClasses = new HashSet<>(entityManager.createQuery("FROM StudentsClasses sc WHERE sc.student = :student", StudentsClasses.class)
                .setParameter("student", student).getResultList());
        Set<StudentsClassesNH> studentsClassesNHS = new HashSet<>(0);
        studentsClasses.forEach(i -> {
            i.setClasses(i.getClasses());
            studentsClassesNHS.add(new StudentsClassesNH(i));
        });
        return studentsClassesNHS;
    }

    private static Object actionForSchoolYearsNH(Object received) {
        Object result = null;
        String action = ((SchoolYearsNH)received).getAction().getAction();
        SchoolYears schoolYears = new SchoolYears((SchoolYearsNH)received);
        SchoolYearsNH schoolYearsNH = (SchoolYearsNH)received;
        switch (action) {
            case "getSemestersToYear":
                Set<Semesters> semesters = new HashSet<>(entityManager.createQuery("FROM Semesters s WHERE s.schoolYear = :year", Semesters.class)
                        .setParameter("year", schoolYears).getResultList());
                Set<SemestersNH> semestersNHS = new HashSet<>(0);
                semesters.forEach(i -> semestersNHS.add(new SemestersNH(i)));
                schoolYearsNH.setSemesters(semestersNHS);
                result = schoolYearsNH;
                break;
            case "remove":
                result = removeSchoolYear(schoolYears);
                break;
        }

        return result;
    }

    private static Object actionForSchoolYears(Object received) {
        Object result = null;
        String action = ((SchoolYears)received).getAction().getAction();
        SchoolYears schoolYears = (SchoolYears)received;
        switch (action) {
            case "saveOrUpdate":
                result = saveOrUpdateSchoolYear(schoolYears);
                break;
        }

        return result;
    }

    private static Object saveOrUpdateSchoolYear(SchoolYears schoolYears) {
        Object result;
        if(schoolYears.getIdSchoolYear() == 0) {
            entityManager.persist(schoolYears);
            entityManager.flush();
            entityManager.refresh(schoolYears);
        } else {
            entityManager.merge(schoolYears);
        }
        entityManager.clear();
        result = schoolYears;
        return result;
    }

    private static Set<Semesters> saveOrUpdateSemester(Set<Semesters> semesters) {
        for (Semesters semester : semesters) {
            entityManager.clear();
            if(semester.getIdSemester() == 0) {
                entityManager.persist(semester);
                entityManager.flush();
                entityManager.refresh(semester);
                semesters.remove(semester);
                semesters.add(semester);
            } else {
                entityManager.merge(semester);
                semesters.remove(semester);
                semesters.add(semester);
            }
        }
        return semesters;
    }

    private static Boolean removeSchoolYear(SchoolYears schoolYears) {
        Boolean result;
        entityManager.remove(entityManager.find(SchoolYears.class, schoolYears.getIdSchoolYear()));
        result = Boolean.TRUE;
        entityManager.flush();
        entityManager.clear();
        return result;
    }

    private static Object actionForGrades(Object received) {
        Object result;
        String action = ((GradesNH)received).getAction().getAction();
        Grades grades = new Grades((GradesNH)received);
        GradesNH gradesNH = (GradesNH)received;
        switch (action) {
            case "getPartialGradesToGrades":
                Set<PartialGrades> partialGrades = new HashSet<>(entityManager.createQuery("FROM PartialGrades pg WHERE pg.grade = :grade", PartialGrades.class)
                        .setParameter("grade", grades).getResultList());
                Set<PartialGradesNH> partialGradesNHS = new HashSet<>(0);
                partialGrades.forEach(i -> partialGradesNHS.add(new PartialGradesNH(i)));
                gradesNH.setPartialGrades(partialGradesNHS);
                break;
            case "update":
                entityManager.merge(grades);
                gradesNH = new GradesNH(grades);
                break;
        }
        result = gradesNH;
        return result;
    }

    private static Object actionForPartialGrades(Object received) {
        Object result = null;
        String action = ((PartialGradesNH) received).getAction().getAction();
        PartialGrades partialGrades = new PartialGrades((PartialGradesNH) received);
        switch (action) {
            case "update":
                entityManager.merge(partialGrades);
                result = Boolean.TRUE;
                break;
            case "save":
                entityManager.persist(partialGrades);
                result = Boolean.TRUE;
                break;
            case "remove":
                entityManager.remove(entityManager.contains(partialGrades) ? partialGrades : entityManager.merge(partialGrades));
                result = Boolean.TRUE;
                break;
        }
        return result;
    }

    private static Object actionForTeachers(Object received) {
        Object result;
        String action = ((TeachersNH) received).getAction().getAction();
        Teachers teachers = new Teachers((TeachersNH)received);
        TeachersNH teachersNH = (TeachersNH)received;
        switch (action) {
            case "getCarriedToTeacher":
                Set<CarriedSubjects> carriedSubjects = new HashSet<>(entityManager.createQuery("FROM CarriedSubjects cs WHERE cs.teacher = :teacher", CarriedSubjects.class)
                        .setParameter("teacher", teachers).getResultList());
                Set<CarriedSubjectsNH> carriedSubjectsNHS = new HashSet<>(0);
                for (CarriedSubjects carriedSubject : carriedSubjects) {
                    CarriedSubjectsNH carriedSubjectsNH = new CarriedSubjectsNH(carriedSubject);
                    carriedSubjectsNH.setSubject(new SubjectsNH(carriedSubject.getSubject()));
                    carriedSubjectsNH.setClasses(new ClassesNH(carriedSubject.getClasses()));
                    carriedSubjectsNH.setSemester(new SemestersNH(carriedSubject.getSemester()));
                    carriedSubjectsNH.getSemester().setSchoolYear(new SchoolYearsNH(carriedSubject.getSemester().getSchoolYear()));

                    Set<Grades> gradesSet = new HashSet<>(entityManager.createQuery("FROM Grades gr WHERE gr.carriedSubjects = :carriedSubject", Grades.class)
                            .setParameter("carriedSubject", carriedSubject).getResultList());

                    Set<GradesNH> gradesNHSet = new HashSet<>(0);

                    gradesSet.forEach((Grades i) -> {
                        GradesNH gradesNH = new GradesNH(i);

                        Set<PartialGrades> partialGrades = new HashSet<>(entityManager.createQuery("FROM PartialGrades pg WHERE pg.grades = :grade", PartialGrades.class)
                                .setParameter("grade", i).getResultList());

                        partialGrades = partialGrades.stream().sorted(Comparator.comparing(PartialGrades::getDate)).collect(Collectors.toSet());

                        Set<PartialGradesNH> partialGradesNHS = new HashSet<>(0);

                        partialGrades.forEach(it -> partialGradesNHS.add(new PartialGradesNH(it)));
                        gradesNH.setPartialGrades(partialGradesNHS);

                        gradesNHSet.add(gradesNH);

                    });

                    carriedSubjectsNH.setGrades(gradesNHSet);
                    carriedSubjectsNHS.add(carriedSubjectsNH);
                }
                teachersNH.setCarriedSubjects(carriedSubjectsNHS);
                break;
        }
        result = teachersNH;
        return result;
    }

    private static Object actionForCarriedSubject(Object received) {
        Object result = null;
        String action = ((CarriedSubjectsNH)received).getAction().getAction();
        CarriedSubjects carriedSubjects = new CarriedSubjects((CarriedSubjectsNH)received);
        CarriedSubjectsNH carriedSubjectsNH = (CarriedSubjectsNH)received;
        switch (action) {
            case "getSemesterToCarriedSubject":
                carriedSubjectsNH.setSemester(new SemestersNH(carriedSubjects.getSemester()));
                result = carriedSubjectsNH;
                break;
            case "saveOrUpdate":
                result = actionForSaveOrUpdateCarriedSubject(carriedSubjects);
                break;
            case "remove":
                result = removeCarriedSubject(carriedSubjects);
                break;
        }
        return result;
    }

    private static Boolean removeCarriedSubject(CarriedSubjects carriedSubjects) {
        Boolean result;
        entityManager.remove(entityManager.find(CarriedSubjects.class, carriedSubjects.getIdCarriedSubject()));
        result = Boolean.TRUE;
        entityManager.flush();
        entityManager.clear();
        return result;
    }

    private static Object actionForSaveOrUpdateCarriedSubject(CarriedSubjects carriedSubjects) {
        Object result;
        if(carriedSubjects.getIdCarriedSubject() == 0) {
            entityManager.persist(carriedSubjects);
            entityManager.flush();
            entityManager.refresh(carriedSubjects);
        } else {
            entityManager.merge(carriedSubjects);
            entityManager.flush();
        }
        entityManager.clear();
        result = carriedSubjects;
        return result;
    }

    private static Object actionForStudentsClasses(Object received) {
        Object result;
        String action = ((StudentsClassesNH) received).getAction().getAction();
        StudentsClasses studentsClasses = new StudentsClasses((StudentsClassesNH) received);
        switch (action) {
            case "getClassToStudentsClasses":
                studentsClasses.setClasses(studentsClasses.getClasses());
                break;
        }
        result = new StudentsClassesNH(studentsClasses);
        return result;
    }

    private static Object actionForClasses(Object received) {
        Object result = null;
        String action = ((ClassesNH) received).getAction().getAction();
        Classes classes = new Classes((ClassesNH)received);
        ClassesNH rec = (ClassesNH)received;
        switch (action) {
            case "getSemesterToClass":
                classes.setSemester(classes.getSemester());
                result = new ClassesNH(classes);
                break;
            case "getCarriedSubjectsToClasses":
                List<CarriedSubjects> carriedSubjects = entityManager.createQuery("FROM CarriedSubjects cs WHERE cs.classes = :classes", CarriedSubjects.class)
                        .setParameter("classes", classes).getResultList();
                Set<CarriedSubjectsNH> res = new HashSet<>(0);
                for (CarriedSubjects i : carriedSubjects) {
                    res.add(new CarriedSubjectsNH(i));
                }
                rec.setCarriedSubjects(res);
                result = rec;
                break;
            case "getStudentsClassesToClasses":
                List<StudentsClasses> studentsClasses = entityManager.createQuery("FROM StudentsClasses sc WHERE sc.classes = :classes", StudentsClasses.class)
                        .setParameter("classes", classes).getResultList();
                Set<StudentsClassesNH> sc = new HashSet<>(0);
                for (StudentsClasses i : studentsClasses) {
                    sc.add(new StudentsClassesNH(i));
                }
                rec.setStudentsClasses(sc);
                result = rec;
                break;
            case "saveOrUpdate":
                result = saveOrUpdateClasses(rec);
                break;
            case "remove":
                result = removeClasses(classes);
                break;
        }
        return result;
    }

    private static Object removeClasses(Classes classes) {
        Boolean result;
        entityManager.remove(entityManager.find(Classes.class, classes.getIdClass()));
        result = Boolean.TRUE;
        entityManager.flush();
        entityManager.clear();
        return result;
    }

    private static ClassesNH saveOrUpdateClasses(ClassesNH rec) {
        Classes classes = new Classes(rec);
        classes.setStudentsClasses(new HashSet<>(0));
        if(classes.getIdClass() == 0) {
            entityManager.persist(classes);
            entityManager.flush();
            entityManager.refresh(classes);
        } else {
            entityManager.merge(classes);
            entityManager.flush();
        }
        rec.getStudentsClasses().forEach(i -> {
            Students student = new Students();
            student.setIdStudent(i.getStudent().getIdStudent());
            Users user = entityManager.createQuery("FROM Users u WHERE student.idStudent = :student", Users.class)
                    .setParameter("student", student.getIdStudent()).getSingleResult();
            student.setUser(user);
            classes.getStudentsClasses().add(new StudentsClasses(i.getIdStudentsClasses(), student, classes));
        });
        entityManager.merge(classes);
        entityManager.flush();
        ClassesNH result = new ClassesNH(classes);
        entityManager.clear();
        return result;
    }

    private static Object actionForSemesters(Object received) {
        Object result;
        String action = ((SemestersNH) received).getAction().getAction();
        Semesters semester = new Semesters((SemestersNH)received);
        switch (action) {
            case "getYearToSemester":
                semester.setSchoolYear(semester.getSchoolYear());
                break;
        }
        result = new SemestersNH(semester);
        return result;
    }

    private static Object actionForClassifieds(Object received) {
        Object result = null;
        String action = ((ClassifiedsNH) received).getAction().getAction();
        Classifieds classified = new Classifieds((ClassifiedsNH)received);
        switch (action) {
            case "getAdminToClassifieds":
                classified.setAdmin(classified.getAdmin());
                result = new ClassifiedsNH(classified);
                break;
            case "remove":
                result = removeClassified(classified);
                break;
            case "saveOrUpdate":
                result = new ClassifiedsNH((Classifieds) saveOrUpdateClassified(classified));
                break;
        }
        return result;
    }

    private static Object saveOrUpdateClassified(Classifieds classified) {
        Object result;
        if(classified.getIdClassifieds() == 0) {
            entityManager.persist(classified);
            entityManager.flush();
            entityManager.refresh(classified);
        }else {
            entityManager.merge(classified);
            entityManager.flush();
        }
        result = classified;
        entityManager.clear();
        return result;
    }

    private static Object removeClassified(Classifieds classifieds) {
        Boolean result;
        entityManager.remove(entityManager.find(Classifieds.class, classifieds.getIdClassifieds()));
        result = Boolean.TRUE;
        entityManager.flush();
        entityManager.clear();
        return result;
    }

    private static Object actionForSubstitutes(Object received) {
        Object result = null;
        String action = ((SubstitutesNH) received).getAction().getAction();
        Substitutes substitute = new Substitutes((SubstitutesNH)received);
        switch (action) {
            case "getAdminToSubstitutes":
                substitute.setAdmin(substitute.getAdmin());
                result = new SubstitutesNH(substitute);
                break;
            case "remove":
                result = removeSubstitute(substitute);
                break;
            case "saveOrUpdate":
                result = new SubstitutesNH((Substitutes) saveOrUpdateSubstitute(substitute));
                break;
        }
        return result;
    }

    private static Object saveOrUpdateSubstitute(Substitutes substitute) {
        Object result;
        if(substitute.getIdSubstitute() == 0) {
            entityManager.persist(substitute);
            entityManager.flush();
            entityManager.refresh(substitute);
        }else {
            entityManager.merge(substitute);
            entityManager.flush();
        }
        result = substitute;
        entityManager.clear();
        return result;
    }

    private static Object removeSubstitute(Substitutes substitute) {
        Boolean result;
        entityManager.remove(entityManager.find(Substitutes.class, substitute.getIdSubstitute()));
        result = Boolean.TRUE;
        entityManager.flush();
        entityManager.clear();
        return result;
    }

    private static Object actionForSubjects(Object received) {
        Object result = null;
        String action = ((SubjectsNH) received).getAction().getAction();
        Subjects subjects = new Subjects((SubjectsNH)received);
        switch (action) {
            case "saveOrUpdate":
                result = new SubjectsNH((Subjects)saveOrUpdateSubject(subjects));
                break;
            case "remove":
                result = removeSubject(subjects);
                break;
        }
        return result;
    }

    private static Object saveOrUpdateSubject(Subjects subjects) {
        Object result;
        if(subjects.getIdSubject() == 0) {
            entityManager.persist(subjects);
            entityManager.flush();
            entityManager.refresh(subjects);
        }else {
            entityManager.merge(subjects);
            entityManager.flush();
        }
        result = subjects;
        entityManager.clear();
        return result;
    }

    private static Object removeSubject(Subjects subjects) {
        Boolean result;
        entityManager.remove(entityManager.find(Subjects.class, subjects.getIdSubject()));
        result = Boolean.TRUE;
        entityManager.flush();
        entityManager.clear();
        return result;
    }

    private static Object actionForAction(Object received) {
        Object result = null;
        switch (((Action) received).getAction()) {
            case "getAllClassifieds":
            case "getAllSubstitutes":
            case "getAllSubjects":
            case "getAllUsers":
            case "getAllSchoolYears":
            case "getAllClasses":
            case "getAllSemesters":
            case "getAllCarriedSubjects":
            case "getAllTeachers":
                result = HibernateUtil.getAll((Action) received);
                break;
        }
        return result;
    }

    static EntityManager getEntityManager() {
        return entityManager;
    }
}
