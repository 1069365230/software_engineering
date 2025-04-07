package at.univie.davidreichert.analyticsandreport.model;

import jakarta.persistence.*;

@Entity
@Table(name = "analysis")
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    @Column(name = "eventId")
    private Long eventId;

    @Column(name = "overallRatingAvg")
    private Double overallRatingAvg;

    @Column(name = "locationRatingAvg")
    private Double locationRatingAvg;

    @Column(name = "descriptionRatingAvg")
    private Double descriptionRatingAvg;


    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Double getOverallRatingAvg() {
        return overallRatingAvg;
    }

    public void setOverallRatingAvg(Double overallRatingAvg) {
        this.overallRatingAvg = overallRatingAvg;
    }

    public Double getLocationRatingAvg() {
        return locationRatingAvg;
    }

    public void setLocationRatingAvg(Double locationRatingAvg) {
        this.locationRatingAvg = locationRatingAvg;
    }

    public Double getDescriptionRatingAvg() {
        return descriptionRatingAvg;
    }

    public void setDescriptionRatingAvg(Double descriptionRatingAvg) {
        this.descriptionRatingAvg = descriptionRatingAvg;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Long analysisId) {
        this.analysisId = analysisId;
    }
}
