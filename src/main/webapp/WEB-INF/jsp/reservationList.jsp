<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="reservations">
    <h2 id="reservations">Reservations</h2>

    <table id="reservationsTable" class="table table-striped" aria-describedby="reservations">
        <thead>
        <tr>
            <th scope="col">Date</th>
            <th scope="col">Time</th>
            <th scope="col">Pet</th>
            <th scope="col">Owner</th>
            <th scope="col">Reason</th>
            <th scope="col">Vet</th>
            <th scope="col">Status</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="reservation" items="${reservations}">
            <tr>
                <td><petclinic:localDate date="${reservation.reservationDate}" pattern="yyyy/MM/dd"/></td>
                <td><c:out value="${reservation.reservationTime}"/></td>
                <td><c:out value="${reservation.pet.name}"/></td>
                <td><c:out value="${reservation.pet.owner.firstName} ${reservation.pet.owner.lastName}"/></td>
                <td><c:out value="${reservation.reason}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${not empty reservation.vet}">
                            <c:out value="${reservation.vet.firstName} ${reservation.vet.lastName}"/>
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>
                <td><c:out value="${reservation.status}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
