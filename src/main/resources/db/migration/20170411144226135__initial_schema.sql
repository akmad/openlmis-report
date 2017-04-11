--
-- Name: jasper_templates; Type: TABLE; Schema: requisition; Owner: postgres; Tablespace:
--

CREATE TABLE jasper_templates (
    id uuid NOT NULL,
    data bytea,
    description text,
    name text NOT NULL,
    type text
);


--
-- Name: template_parameters; Type: TABLE; Schema: requisition; Owner: postgres; Tablespace:
--

CREATE TABLE template_parameters (
    id uuid NOT NULL,
    datatype text,
    defaultvalue text,
    description text,
    displayname text,
    name text,
    selectExpression text,
    selectProperty text,
    displayProperty text,
    required boolean,
    templateid uuid NOT NULL
);


--
-- Name: jasper_templates_pkey; Type: CONSTRAINT; Schema: requisition; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY jasper_templates
    ADD CONSTRAINT jasper_templates_pkey PRIMARY KEY (id);


--
-- Name: template_parameters_pkey; Type: CONSTRAINT; Schema: requisition; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY template_parameters
    ADD CONSTRAINT template_parameters_pkey PRIMARY KEY (id);


--
-- Name: uk_5878s5vb2v4y53vun95nrdvgw; Type: CONSTRAINT; Schema: requisition; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY jasper_templates
    ADD CONSTRAINT uk_5878s5vb2v4y53vun95nrdvgw UNIQUE (name);


--
-- Name: fk_qww3p7ho2t5jyutkllrh64khr; Type: FK CONSTRAINT; Schema: requisition; Owner: postgres
--

ALTER TABLE ONLY template_parameters
    ADD CONSTRAINT fk_qww3p7ho2t5jyutkllrh64khr FOREIGN KEY (templateid) REFERENCES jasper_templates(id);
