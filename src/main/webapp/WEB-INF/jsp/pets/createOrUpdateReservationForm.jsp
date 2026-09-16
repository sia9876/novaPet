<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>


<petclinic:layout pageName="owners">
    <jsp:attribute name="customScript">
        <script>
            $(function () {
                $("#reservationDate").datepicker({dateFormat: 'yy/mm/dd'});
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <h2><c:if test="${reservation['new']}">New </c:if>Reservation</h2>

        <span id="pet"><strong>Pet</strong></span>
        <table class="table table-striped" aria-describedby="pet">
            <thead>
            <tr>
                <th scope="col">Name</th>
                <th scope="col">Birth Date</th>
                <th scope="col">Type</th>
                <th scope="col">Owner</th>
            </tr>
            </thead>
            <tr>
                <td><c:out value="${reservation.pet.name}"/></td>
                <td><petclinic:localDate date="${reservation.pet.birthDate}" pattern="yyyy/MM/dd"/></td>
                <td><c:out value="${reservation.pet.type.name}"/></td>
                <td><c:out value="${reservation.pet.owner.firstName} ${reservation.pet.owner.lastName}"/></td>
            </tr>
        </table>

        <form:form modelAttribute="reservation" class="form-horizontal">
            <div class="form-group has-feedback">
                <petclinic:inputField label="Date" name="reservationDate"/>
                <petclinic:inputField label="Time (HH:mm)" name="reservationTime"/>
                <petclinic:inputField label="Reason" name="reason"/>

                <div class="form-group">
                    <label class="col-sm-2 control-label">Preferred Vet</label>
                    <div class="col-sm-10">
                        <form:select class="form-control" path="vet" itemValue="id" itemLabel="lastName"
                                     items="${vets}">
                            <form:option value="">-- No preference --</form:option>
                        </form:select>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <input type="hidden" name="petId" value="${reservation.pet.id}"/>
                    <button class="btn btn-default" type="submit">Request Reservation</button>
                </div>
            </div>
        </form:form>

        <br/>
        <strong id="upcomingReservations">Upcoming Reservations</strong>
        <table class="table table-striped" aria-describedby="upcomingReservations">
            <tr>
                <th scope="col">Date</th>
                <th scope="col">Time</th>
                <th scope="col">Reason</th>
                <th scope="col">Vet</th>
                <th scope="col">Status</th>
            </tr>
            <c:forEach var="reservation" items="${reservation.pet.reservations}">
                <c:if test="${!reservation['new']}">
                    <tr>
                        <td><petclinic:localDate date="${reservation.reservationDate}" pattern="yyyy/MM/dd"/></td>
                        <td><c:out value="${reservation.reservationTime}"/></td>
                        <td><c:out value="${reservation.reason}"/></td>
                        <td><c:out value="${reservation.vet.firstName} ${reservation.vet.lastName}"/></td>
                        <td><c:out value="${reservation.status}"/></td>
                    </tr>
                </c:if>
            </c:forEach>
        </table>
    </jsp:body>

</petclinic:layout>
