package org.clinepi.service.services;

import static org.gusdb.fgputil.db.stream.ResultSetInputStream.getResultSetStream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.gusdb.fgputil.db.stream.ResultSetToNdJsonConverter;
import org.gusdb.fgputil.functional.Functions;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.service.service.AbstractWdkService;

@Path("/shiny")
public class ShinyQueryService extends AbstractWdkService {

  @SuppressWarnings("unused")
  private static final Logger LOG = Logger.getLogger(ShinyQueryService.class);

  private Response getStreamingResponse(String sql, String queryName, String errorMsgOnFail) throws WdkModelException {
    return Response.ok(
      getStreamingOutput(
        Functions.mapException(
          () -> getResultSetStream(sql, queryName, getWdkModel().getAppDb().getDataSource(), new ResultSetToNdJsonConverter()),
          e -> new WdkModelException(errorMsgOnFail + " SQL: " + sql, e)
        )
      )
    ).build();
  }

  @GET
  @Path("Participant/{tblPrefix}/{sourceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getShinyParticipantData(
      @PathParam("sourceId") String sourceId,
      @PathParam("tblPrefix") String tblPrefix,
      @DefaultValue("none") @QueryParam("timeSourceId") String timeSourceId)
          throws WdkModelException {

    String sql = timeSourceId.equals("none")
        ? " select pa.name as Participant_Id" + 
                ", pa." + sourceId +
          " from apidbtuning." + tblPrefix + "Participants pa" +
          " where pa." + sourceId + " is not null"
        : " select pa.name as Participant_Id" +
                ", pa." + sourceId + 
                ", oa." + timeSourceId +
          " from apidbtuning." + tblPrefix + "Participants pa" +
              ", apidbtuning." + tblPrefix + "Observations oa" +
              ", apidbtuning." + tblPrefix + "PartObsIO io" +
          " where pa.pan_id = io.participant_id" +
          " and io.observation_id = oa.pan_id" +
          " and pa." + sourceId + " is not null";

    return getStreamingResponse(sql, "getShinyParticipantData",
        "Failed running SQL to fetch participant data.");
  }

  @GET
  @Path("Household/{tblPrefix}/{sourceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getShinyHouseholdData(
      @PathParam("sourceId") String sourceId,
      @PathParam("tblPrefix") String tblPrefix,
      @DefaultValue("none") @QueryParam("timeSourceId") String timeSourceId) 
          throws WdkModelException {

    // have to remember when calling this that timeSourceId representing time is different for households
    // so ask for a different sourceId, the houseObs equivalent of timeSourceId (ex: gems BFO_0000015->EUPATH_0015467)
    String sql = timeSourceId.equals("none")
            ? " select pa.name as Participant_Id" +
                    ", h.string_value as " + sourceId +
              " from apidbtuning." + tblPrefix + "HouseholdMD h" +
                  ", apidbtuning." + tblPrefix + "HousePartIO hp" +
                  ", apidbtuning." + tblPrefix + "Participants pa" +
              " where h.ontology_term_name = '" + sourceId + "'" +
              " and hp.household_id = h.household_id" +
              " and hp.participant_id = pa.pan_id"   

            : " with indicator as (" +
                " select CASE WHEN (count(*) > 0) THEN 0" +
                       " ELSE 1 END as isHouseOb" +
                " from apidbtuning." + tblPrefix + "HouseholdMD" +     
                " where ontology_term_name = '" + sourceId + "'" +     
                " and household_id = household_observation_id)," +
              " term as (" +
                " select pa.name as Participant_Id" +
                      ", h.string_value as " + sourceId +
                      ", CASE WHEN (i.ishouseob = 0) THEN h.household_id" +
                       " ELSE h.household_observation_id END as key" +
                " from apidbtuning." + tblPrefix + "HouseholdMD h" +
                    ", apidbtuning." + tblPrefix + "HousePartIO hp" +
                    ", apidbtuning." + tblPrefix + "Participants pa" +
		    ", indicator i" +
                " where h.ontology_term_name = '" + sourceId + "'" +
                " and hp.household_id = h.household_id" +
                " and hp.participant_id = pa.pan_id)," +
              " time as (" + 
                " select pa.name as Participant_Id" +
                      ", h.string_value as " + timeSourceId +
                      ", CASE WHEN (i.ishouseob = 0) THEN h.household_id" +
                       " ELSE h.household_observation_id END as key" +
                " from apidbtuning." + tblPrefix + "HouseholdMD h" +
                    ", apidbtuning." + tblPrefix + "HousePartIO hp" +
                    ", apidbtuning." + tblPrefix + "Participants pa" +
		    ", indicator i" +
                " where h.ontology_term_name = '" + timeSourceId + "'" +
                " and hp.household_id = h.household_id" +
                " and hp.participant_id = pa.pan_id)" +
              " select term.Participant_Id" +
                    ", term." + sourceId +
                    ", time." + timeSourceId +
              " from time, term" +
              " where term.key = time.key";

    return getStreamingResponse(sql, "getShinyHouseholdData",
        "Failed running SQL to fetch household data.");
  }

  @GET
  @Path("Observation/{tblPrefix}/{sourceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getShinyObservationData(
      @PathParam("sourceId") String sourceId,
      @PathParam("tblPrefix") String tblPrefix,
      @DefaultValue("none") @QueryParam("timeSourceId") String timeSourceId) 
          throws WdkModelException {

    String sql = timeSourceId.equals("none")
            ? " select pa.name as participant_id" +
                    ", string_value as " + sourceId +
              " from apidbtuning." + tblPrefix + "ObservationMD o" +
                  ", apidbtuning." + tblPrefix + "Participants pa" +
              " where o.ontology_term_name = '" + sourceId + "'" +
              " and o.participant_id = pa.pan_id"
            : " with term as (" +
                " select pa.name as participant_id" +
                      ", string_value as " + sourceId +
                      ", sub_observation_id" +  
                " from apidbtuning." + tblPrefix + "ObservationMD o" +
                    ", apidbtuning." + tblPrefix + "Participants pa" +
                " where o.ontology_term_name = '" + sourceId + "'" +
                " and o.participant_id = pa.pan_id)," +
              " time as (" +
                " select pa.name as participant_id" +
                      ", string_value as " + timeSourceId +
                      ", sub_observation_id" +
                " from apidbtuning." + tblPrefix + "ObservationMD o" +
                    ", apidbtuning." + tblPrefix + "Participants pa" +
                " where o.ontology_term_name = '" + timeSourceId + "'" +
                " and o.participant_id = pa.pan_id)" +
              " select term.participant_id" +
                    ", term." + sourceId +
                    ", time." + timeSourceId + 
              " from time, term" + 
              " where term.sub_observation_id = time.sub_observation_id";

    return getStreamingResponse(sql, "getShinyObservationData",
        "Failed running SQL to fetch observation data.");
  }

  @GET
  @Path("ObservationNames/{tblPrefix}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getShinyObservationNamesData(
      @PathParam("tblPrefix") String tblPrefix,
      @DefaultValue("none") @QueryParam("timeSourceId") String timeSourceId) 
          throws WdkModelException {

    String sql = timeSourceId.equals("none")
            ? " select pa.name as participant_id" +
                    ", oa.name as observation_id" +
              " from apidbtuning." + tblPrefix + "Observations oa" +
                  ", apidbtuning." + tblPrefix + "Participants pa" +
                  ", apidbtuning." + tblPrefix + "PartObsIO po" +  
              " where oa.pan_id = po.observation_id" +
              " and po.participant_id = pa.pan_id"
            : " select pa.name as participant_id" +
                    ", oa.name as observation_id" +
                    ", oa." + timeSourceId + 
              " from apidbtuning." + tblPrefix + "Observations oa" +
                  ", apidbtuning." + tblPrefix + "Participants pa" +
                  ", apidbtuning." + tblPrefix + "PartObsIO po" +
              " where oa.pan_id = po.observation_id" +
              " and po.participant_id = pa.pan_id";

    return getStreamingResponse(sql, "getShinyObservationNamesData",
        "Failed running SQL to fetch observation data.");
  }

  @GET
  @Path("Sample/{tblPrefix}/{sourceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getShinySampleData(
      @PathParam("sourceId") String sourceId,
      @PathParam("tblPrefix") String tblPrefix,
      @DefaultValue("none") @QueryParam("timeSourceId") String timeSourceId)
          throws WdkModelException {

    String sql = timeSourceId.equals("none")
            ? " select pa.name as Participant_Id, sa." + sourceId +
              " from apidbtuning." + tblPrefix + "Participants pa" +
                  ", apidbtuning." + tblPrefix + "Samples sa "+
                  ", apidbtuning." + tblPrefix + "PartObsIO io" +
                  ", apidbtuning." + tblPrefix + "ObsSampleIO io2" +
              " where pa.pan_id = io.participant_id" +
              " and io.observation_id = io2.observation_id" +
              " and io2.sample_id = sa.pan_id" +
              " and sa." + sourceId + " is not null"
            : " select pa.name as Participant_Id" +
                    ", sa." + sourceId +
                    ", oa." + timeSourceId +
              " from apidbtuning." + tblPrefix + "Participants pa" +
                  ", apidbtuning." + tblPrefix + "Observations oa" +
                  ", apidbtuning." + tblPrefix + "Samples sa "+
                  ", apidbtuning." + tblPrefix + "PartObsIO io" +
                  ", apidbtuning." + tblPrefix + "ObsSampleIO io2" +
              " where pa.pan_id = io.participant_id" +
              " and io.observation_id = oa.pan_id" +
              " and io.observation_id = io2.observation_id" +
              " and io2.sample_id = sa.pan_id" +
              " and sa." + sourceId + " is not null";

    return getStreamingResponse(sql, "getShinySampleData",
        "Failed running SQL to fetch sample data.");
  }
}
