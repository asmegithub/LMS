# LMS Model Relationships & Implementation Order

This document describes entity relationships in `com.EGM.LMS.model` and suggests an order to implement or wire features one by one.

---

## 1. Core Entity Relationship Map

### 1.1 User & Auth
```
User (standalone)
  ├── role: String (STUDENT, INSTRUCTOR, ADMIN)
  ├── referralCode, referredBy (self-referral)
  └── No JPA relations to other entities from User side; others reference User via @ManyToOne

InstructorProfile
  └── @ManyToOne User user

UserSession
  └── @ManyToOne User user
```

### 1.2 Course Hierarchy
```
User (as instructor) → InstructorProfile (1:1 logical)
InstructorProfile
  └── (inverse) Course.instructor

CourseCategory (standalone, optional parentId for hierarchy)
  └── (inverse) Course.category

Course
  ├── @ManyToOne InstructorProfile instructor
  ├── @ManyToOne CourseCategory category
  └── (inverse) CourseSection.course

CourseSection
  ├── @ManyToOne Course course
  └── (inverse) Lesson.section

Lesson
  ├── @ManyToOne CourseSection section
  ├── (inverse) LessonResource.lesson
  ├── (inverse) LessonDiscussion.lesson
  ├── (inverse) LessonNote.lesson
  ├── (inverse) LessonProgress.lesson
  ├── (inverse) VideoProgress.lesson
  ├── (inverse) Quiz.lesson (1:1 logical)
  └── (inverse) Bookmark.lesson, Download.lesson
```

### 1.3 Course Metadata (Course-scoped)
```
CourseOutcome    → @ManyToOne Course course
CourseRequirement → @ManyToOne Course course
CourseApproval   → @ManyToOne Course course, @ManyToOne User reviewer
CourseRating     → @ManyToOne Course course   [aggregate; separate from Review]
Review           → @ManyToOne Course course, @ManyToOne User student
```

### 1.4 Enrollment & Learning Progress
```
Enrollment
  ├── @ManyToOne User student
  ├── @ManyToOne Course course
  ├── @ManyToOne Payment payment (optional; null for free enrollments)
  ├── progress, completedLessonsCount, lastAccessedLessonId, isCompleted
  └── (inverse) Certificate.enrollment, LessonProgress.enrollment, VideoProgress.enrollment

LessonProgress   → Enrollment, Lesson, User (student)
VideoProgress   → Enrollment, Lesson, User (student)
```

### 1.5 Payments & Money Flow
```
Payment
  ├── @ManyToOne User student
  ├── @ManyToOne Course course
  ├── @ManyToOne Coupon coupon (optional)
  ├── transactionId, gateway, status, amount, netAmount, platformShare, instructorShare
  └── (inverse) Enrollment.payment, PaymentTransaction.payment, Refund.payment

PaymentTransaction → @ManyToOne Payment payment  (granular gateway txns)
Refund             → @ManyToOne Payment payment
Coupon             (standalone; referenced by Payment)
```

### 1.6 Instructor Money
```
InstructorProfile
  └── (inverse) InstructorEarning.instructorProfile, InstructorBankDetail.instructorProfile, Payout.instructorProfile

InstructorEarning   → @ManyToOne InstructorProfile (totals, balance)
InstructorBankDetail → @ManyToOne InstructorProfile (bank account for payouts)
Payout              → @ManyToOne InstructorProfile (each withdrawal)
```

### 1.7 Quizzes & Assessments
```
Quiz
  └── @ManyToOne Lesson lesson
      └── (inverse) Question.quiz

Question
  └── @ManyToOne Quiz quiz
      └── (inverse) QuestionOption.question

QuestionOption → @ManyToOne Question question

QuizAttempt
  ├── @ManyToOne User student
  ├── @ManyToOne Quiz quiz
  └── score, totalPoints, maxPoints, isPassed, attemptNumber

QuizAnswer
  ├── @ManyToOne QuizAttempt attempt
  ├── @ManyToOne Question question
  └── @ManyToOne QuestionOption selectedOption
```

### 1.8 Discussions (Q&A)
```
LessonDiscussion
  ├── @ManyToOne Lesson lesson
  ├── @ManyToOne User user
  └── (inverse) DiscussionReply.discussion

DiscussionReply
  ├── @ManyToOne LessonDiscussion discussion
  └── @ManyToOne User user
```

### 1.9 Certificates
```
Certificate
  ├── @ManyToOne Enrollment enrollment
  ├── @ManyToOne User student
  ├── @ManyToOne Course course
  └── @ManyToOne CertificateTemplate template

CertificateTemplate (standalone)
```

### 1.10 Wishlist, Bookmark, Referral
```
Wishlist  → @ManyToOne User user, @ManyToOne Course course
Bookmark  → @ManyToOne User user, @ManyToOne Course course, @ManyToOne Lesson lesson, timestamp, note
Referral  → @ManyToOne User referrer, @ManyToOne User referee
ReferralReward → @ManyToOne Referral referral, @ManyToOne User user
```

### 1.11 Notifications & System
```
Notification → @ManyToOne User user (type, title, message, isRead, relatedId, actionUrl)
EmailLog     → @ManyToOne User recipient
AuditLog     → @ManyToOne User admin
SearchHistory → @ManyToOne User user
Download     → @ManyToOne User user, @ManyToOne Lesson lesson
LessonNote   → @ManyToOne Lesson lesson, @ManyToOne User student
```

### 1.12 RBAC
```
Role
  └── (inverse) RolePermission.role
Permission
  └── (inverse) RolePermission.permission
RolePermission → @ManyToOne Role role, @ManyToOne Permission permission
```
(Note: `User.role` is a String; if you need full RBAC, you’d add User ↔ Role relation.)

---

## 2. Implementation Order (One by One)

Implement in this order so that dependencies exist before they’re used.

### Phase 1 – Already reflected in frontend (verify/match backend)
1. **User & Auth** – User, AuthController, JWT, sessions.
2. **InstructorProfile** – Apply, verify, “me”; used by Course and enrollments.
3. **CourseCategory** – CRUD; used by Course filters and create.
4. **Course** – CRUD, list by status (e.g. APPROVED); instructor filter by profile.
5. **CourseSection** – By courseId (filter in API).
6. **Lesson** – By section/course; types VIDEO, DOCUMENT, TEXT, QUIZ.
7. **LessonResource** – By lesson.
8. **CourseOutcome & CourseRequirement** – By course.
9. **Enrollment** – Create (with optional paymentId), list “me”, delete; update progress/lastAccessedLessonId when implementing learner UI.
10. **Review** – By course; create/update/delete.
11. **LessonDiscussion & DiscussionReply** – By lesson; create, list.
12. **Quiz, Question, QuestionOption** – By lesson; CRUD (instructor); ensure course/section/lesson hierarchy is clear.
13. **Notification** – By user; list, mark read.
14. **Certificate** – By user/enrollment; list, generate when course completed.
15. **CourseApproval** – Admin workflow; approve/reject course.

### Phase 2 – Payments & instructor money
16. **Coupon** – CRUD (admin); validate in payment flow.
17. **Payment** – Create (checkout), list by user (student), list all (admin); link to Enrollment.
18. **PaymentTransaction** – Create per gateway call; list for admin/payout reconciliation.
19. **Refund** – Create (admin) from Payment; update Payment status.
20. **InstructorEarning** – Aggregate from payments; read for instructor dashboard.
21. **InstructorBankDetail** – CRUD by instructor; use for Payout.
22. **Payout** – Create (request), list by instructor; update status (admin).

### Phase 3 – Learning experience (course player)
23. **LessonProgress** – Upsert when student completes/opens lesson; recalc Enrollment.progress and completedLessonsCount.
24. **VideoProgress** – Upsert on pause/complete (watchedDuration, lastWatchedPosition); optional for “resume”.
25. **QuizAttempt** – Create when student starts quiz; submit answers.
26. **QuizAnswer** – Create per question on attempt submit; grade and set QuizAttempt.score, isPassed.

### Phase 4 – Engagement & growth
27. **Wishlist** – Add/remove by user+course; list “me”.
28. **Bookmark** – CRUD by user+lesson; optional timestamp/note.
29. **Referral** – Create on signup if referredBy; update on first purchase.
30. **ReferralReward** – Create when conditions met; pay out (or mark paid).
31. **SearchHistory** – Create on search; list “me” for recent searches.

### Phase 5 – Admin & system
32. **Admin: Users** – List/update User (UserController).
33. **Admin: Enrollments** – List all (EnrollmentController).
34. **Admin: Payments** – List Payment/PaymentTransaction; filters.
35. **AuditLog** – Write on sensitive actions; list (admin).
36. **EmailLog** – Write on send; list (admin).
37. **SystemSetting** – CRUD (admin); read public keys in app.
38. **Role, Permission, RolePermission** – If moving from User.role string to RBAC; assign roles to users.

### Phase 6 – Optional / polish
39. **CourseRating** – Aggregate from Review (or separate rating field); cache on Course.
40. **CertificateTemplate** – CRUD (admin); choose template when issuing Certificate.
41. **LessonNote** – CRUD by student per lesson.
42. **Download** – Track offline downloads (if you support them).

---

## 3. API Contract Notes (Frontend ↔ Backend)

- **Filter by course**: Sections, lessons, outcomes, requirements, discussions should support `courseId` (or course slug) so the frontend doesn’t load all then filter.
- **Enrollment progress**: When LessonProgress or VideoProgress is updated, backend should recompute `Enrollment.progress` and `completedLessonsCount` and set `lastAccessedLessonId`.
- **Payment → Enrollment**: Checkout should create Payment (and optionally PaymentTransaction), then create Enrollment with `payment_id` set.
- **Ids**: All IDs are UUID (CHAR(36)); frontend already uses string IDs.

Use this order to implement or wire features one by one while respecting model relationships.
