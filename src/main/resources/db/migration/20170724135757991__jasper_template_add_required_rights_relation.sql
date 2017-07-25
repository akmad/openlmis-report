--
-- Name: jaspertemplate_requiredrights; Type: TABLE; Schema: report; Owner: postgres
--

CREATE TABLE jaspertemplate_requiredrights (
    jaspertemplateid uuid NOT NULL,
    requiredrights character varying(255)
);

--
-- Name: jaspertemplate_requiredrights pk_jaspertemplate_requiredrights__jasper_templates; Type: PK CONSTRAINT; Schema: report; Owner: postgres
--

ALTER TABLE ONLY jaspertemplate_requiredrights
    ADD CONSTRAINT pk_jaspertemplate_requiredrights__jasper_templates
    FOREIGN KEY (jaspertemplateid) REFERENCES jasper_templates(id);
