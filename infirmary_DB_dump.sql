--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2 (Debian 17.2-1.pgdg120+1)
-- Dumped by pg_dump version 17.2 (Debian 17.2-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: ad; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.ad (
    ad_email character varying(255) NOT NULL,
    designation character varying(255),
    name character varying(255) NOT NULL,
    password character varying(255),
    location bigint
);


ALTER TABLE public.ad OWNER TO infirmary_db_user;;

--
-- Name: ad_prescription; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.ad_prescription (
    id uuid NOT NULL,
    date date,
    quantity integer,
    "timestamp" bigint,
    ad_ad_email character varying(255),
    medicine_stock_id uuid,
    patient_sap_email character varying(255)
);


ALTER TABLE public.ad_prescription OWNER TO infirmary_db_user;;

--
-- Name: admin; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.admin (
    admin_email character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    password character varying(255)
);


ALTER TABLE public.admin OWNER TO infirmary_db_user;;

--
-- Name: appointment; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.appointment (
    appointment_id uuid NOT NULL,
    date date NOT NULL,
    temperature real,
    "timestamp" bigint,
    token_no integer,
    weight real,
    apt_form uuid,
    doctor_email character varying(255),
    location bigint,
    sap_email character varying(255) NOT NULL,
    prescription_id uuid
);


ALTER TABLE public.appointment OWNER TO infirmary_db_user;;

--
-- Name: appointment_form; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.appointment_form (
    id uuid NOT NULL,
    is_follow_up boolean DEFAULT false,
    reason character varying(255) NOT NULL,
    reason_for_pref character varying(255),
    pref_doctor_doctor_id uuid,
    prev_appointment uuid
);


ALTER TABLE public.appointment_form OWNER TO infirmary_db_user;;

--
-- Name: conformation; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.conformation (
    conformation_token uuid NOT NULL,
    "timestamp" bigint,
    ad_ad_email character varying(255),
    doctor_doctor_id uuid,
    patient_sap_email character varying(255)
);


ALTER TABLE public.conformation OWNER TO infirmary_db_user;;

--
-- Name: current_appointment; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.current_appointment (
    current_appointment_id uuid NOT NULL,
    appointment_id uuid,
    doctor_email character varying(255),
    sap_email character varying(255)
);


ALTER TABLE public.current_appointment OWNER TO infirmary_db_user;;

--
-- Name: doctor; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.doctor (
    doctor_id uuid NOT NULL,
    designation character varying(255),
    doctor_email character varying(255),
    gender character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    password character varying(255),
    status boolean,
    location bigint
);


ALTER TABLE public.doctor OWNER TO infirmary_db_user;;

--
-- Name: locations; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.locations (
    location_id bigint NOT NULL,
    latitude double precision,
    location_name character varying(255),
    longitude double precision
);


ALTER TABLE public.locations OWNER TO infirmary_db_user;;

--
-- Name: locations_location_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

ALTER TABLE public.locations ALTER COLUMN location_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.locations_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: medical_details; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.medical_details (
    id uuid NOT NULL,
    allergies character varying(255) NOT NULL,
    current_address character varying(255) NOT NULL,
    family_medical_history character varying(255) NOT NULL,
    height real NOT NULL,
    medical_history character varying(255) NOT NULL,
    residence_type character varying(255),
    weight real NOT NULL,
    sap_email character varying(255)
);


ALTER TABLE public.medical_details OWNER TO infirmary_db_user;;

--
-- Name: password_change; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.password_change (
    change_code uuid NOT NULL,
    ad_ad_email character varying(255),
    doctor_doctor_id uuid,
    patient_sap_email character varying(255)
);


ALTER TABLE public.password_change OWNER TO infirmary_db_user;;

--
-- Name: patient; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.patient (
    sap_email character varying(255) NOT NULL,
    blood_group character varying(255),
    date_of_birth date NOT NULL,
    emergency_contact character varying(255) NOT NULL,
    gender character varying(255),
    image_url character varying(255),
    name character varying(255) NOT NULL,
    password character varying(255),
    phone_number character varying(255) NOT NULL,
    program character varying(255) NOT NULL,
    sap_id character varying(255),
    school character varying(255) NOT NULL
);


ALTER TABLE public.patient OWNER TO infirmary_db_user;;

--
-- Name: patient_medical_details_mapping; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.patient_medical_details_mapping (
    id uuid NOT NULL,
    medical_details_id uuid NOT NULL,
    sap_email character varying(255) NOT NULL
);


ALTER TABLE public.patient_medical_details_mapping OWNER TO infirmary_db_user;;

--
-- Name: prescription; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.prescription (
    prescription_id uuid NOT NULL,
    diagnosis character varying(255),
    dietary_remarks character varying(255),
    test_needed character varying(255),
    doctor_email character varying(255),
    sap_email character varying(255)
);


ALTER TABLE public.prescription OWNER TO infirmary_db_user;;

--
-- Name: prescription_medicine; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.prescription_medicine (
    pres_medicine_id uuid NOT NULL,
    dosage_afternoon real,
    dosage_evening real,
    dosage_morning real,
    duration integer,
    suggestion character varying(255),
    medicine uuid,
    prescription uuid
);


ALTER TABLE public.prescription_medicine OWNER TO infirmary_db_user;;

--
-- Name: stocks; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.stocks (
    stock_id uuid NOT NULL,
    batch_number bigint,
    company character varying(255) NOT NULL,
    composition character varying(255) NOT NULL,
    expiration_date date NOT NULL,
    medicine_name character varying(255) NOT NULL,
    medicine_type character varying(255) NOT NULL,
    quantity bigint NOT NULL,
    location bigint
);


ALTER TABLE public.stocks OWNER TO infirmary_db_user;;

--
-- Data for Name: ad; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.ad (ad_email, designation, name, password, location) FROM stdin;
\.


--
-- Data for Name: ad_prescription; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.ad_prescription (id, date, quantity, "timestamp", ad_ad_email, medicine_stock_id, patient_sap_email) FROM stdin;
\.


--
-- Data for Name: admin; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.admin (admin_email, name, password) FROM stdin;
admin@ddn.upes.ac.in	Admin 1	$2a$10$H5WNiPsof1/R6WiD2aJyyeaDtfLxoxkwY2TkhY9oXiNQvydLq/l/e
\.


--
-- Data for Name: appointment; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.appointment (appointment_id, date, temperature, "timestamp", token_no, weight, apt_form, doctor_email, location, sap_email, prescription_id) FROM stdin;
\.


--
-- Data for Name: appointment_form; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.appointment_form (id, is_follow_up, reason, reason_for_pref, pref_doctor_doctor_id, prev_appointment) FROM stdin;
\.


--
-- Data for Name: conformation; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.conformation (conformation_token, "timestamp", ad_ad_email, doctor_doctor_id, patient_sap_email) FROM stdin;
\.


--
-- Data for Name: current_appointment; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.current_appointment (current_appointment_id, appointment_id, doctor_email, sap_email) FROM stdin;
\.


--
-- Data for Name: doctor; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.doctor (doctor_id, designation, doctor_email, gender, name, password, status, location) FROM stdin;
\.


--
-- Data for Name: locations; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.locations (location_id, latitude, location_name, longitude) FROM stdin;
1	30.416944	UPES Bidholi Campus	77.967615
2	30.383406	UPES Kandoli Campus	77.968811
\.


--
-- Data for Name: medical_details; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.medical_details (id, allergies, current_address, family_medical_history, height, medical_history, residence_type, weight, sap_email) FROM stdin;
\.


--
-- Data for Name: password_change; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.password_change (change_code, ad_ad_email, doctor_doctor_id, patient_sap_email) FROM stdin;
\.


--
-- Data for Name: patient; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.patient (sap_email, blood_group, date_of_birth, emergency_contact, gender, image_url, name, password, phone_number, program, sap_id, school) FROM stdin;
aadityavv9@gmail.com	A+	2003-12-03	1234567890	Male	\N	Aaditya Vijayvargiya	$2a$10$MoEHoKeCTd/EKdc0r6EAPOdNnAS164KL2jKU5Wc1i3FK.T2Q1Fzmm	6307692868	B.Tech	500105423	SOCS
aadi.engineering03@gmail.com	A+	2002-02-12	6789054321	Male	Profile/aadi_engineering03@gmail_com.jpeg	Aaditya Vijayvargiya	$2a$10$olC3ap020ANj9SLZSEx6Fu6iydBe5uB5vEi8MHoHY4DItlNKBKG/G	9876543210	B.Tech	500104532	SOCS
\.


--
-- Data for Name: patient_medical_details_mapping; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.patient_medical_details_mapping (id, medical_details_id, sap_email) FROM stdin;
\.


--
-- Data for Name: prescription; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.prescription (prescription_id, diagnosis, dietary_remarks, test_needed, doctor_email, sap_email) FROM stdin;
\.


--
-- Data for Name: prescription_medicine; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.prescription_medicine (pres_medicine_id, dosage_afternoon, dosage_evening, dosage_morning, duration, suggestion, medicine, prescription) FROM stdin;
\.


--
-- Data for Name: stocks; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.stocks (stock_id, batch_number, company, composition, expiration_date, medicine_name, medicine_type, quantity, location) FROM stdin;
\.


--
-- Name: locations_location_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.locations_location_id_seq', 2, true);


--
-- Name: ad ad_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ad
    ADD CONSTRAINT ad_pkey PRIMARY KEY (ad_email);


--
-- Name: ad_prescription ad_prescription_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ad_prescription
    ADD CONSTRAINT ad_prescription_pkey PRIMARY KEY (id);


--
-- Name: admin admin_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT admin_pkey PRIMARY KEY (admin_email);


--
-- Name: appointment_form appointment_form_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment_form
    ADD CONSTRAINT appointment_form_pkey PRIMARY KEY (id);


--
-- Name: appointment appointment_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT appointment_pkey PRIMARY KEY (appointment_id);


--
-- Name: conformation conformation_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.conformation
    ADD CONSTRAINT conformation_pkey PRIMARY KEY (conformation_token);


--
-- Name: current_appointment current_appointment_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.current_appointment
    ADD CONSTRAINT current_appointment_pkey PRIMARY KEY (current_appointment_id);


--
-- Name: doctor doctor_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.doctor
    ADD CONSTRAINT doctor_pkey PRIMARY KEY (doctor_id);


--
-- Name: locations locations_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (location_id);


--
-- Name: medical_details medical_details_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.medical_details
    ADD CONSTRAINT medical_details_pkey PRIMARY KEY (id);


--
-- Name: password_change password_change_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.password_change
    ADD CONSTRAINT password_change_pkey PRIMARY KEY (change_code);


--
-- Name: patient_medical_details_mapping patient_medical_details_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.patient_medical_details_mapping
    ADD CONSTRAINT patient_medical_details_mapping_pkey PRIMARY KEY (id);


--
-- Name: patient patient_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.patient
    ADD CONSTRAINT patient_pkey PRIMARY KEY (sap_email);


--
-- Name: prescription_medicine prescription_medicine_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prescription_medicine
    ADD CONSTRAINT prescription_medicine_pkey PRIMARY KEY (pres_medicine_id);


--
-- Name: prescription prescription_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prescription
    ADD CONSTRAINT prescription_pkey PRIMARY KEY (prescription_id);


--
-- Name: stocks stocks_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.stocks
    ADD CONSTRAINT stocks_pkey PRIMARY KEY (stock_id);


--
-- Name: patient_medical_details_mapping uk1ssgb1ygkmogg4wn5sh2fym2j; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.patient_medical_details_mapping
    ADD CONSTRAINT uk1ssgb1ygkmogg4wn5sh2fym2j UNIQUE (sap_email);


--
-- Name: patient_medical_details_mapping uk2d0bgdgg6bqi5klu754l6fktx; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.patient_medical_details_mapping
    ADD CONSTRAINT uk2d0bgdgg6bqi5klu754l6fktx UNIQUE (medical_details_id);


--
-- Name: conformation ukcq2nbjjbnfvwtnj8ojuf5qrch; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.conformation
    ADD CONSTRAINT ukcq2nbjjbnfvwtnj8ojuf5qrch UNIQUE (patient_sap_email);


--
-- Name: appointment uker59wusnnjfmaj8bollao1aee; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT uker59wusnnjfmaj8bollao1aee UNIQUE (apt_form);


--
-- Name: conformation ukhjsugd88p0bpmartsglw2avvh; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.conformation
    ADD CONSTRAINT ukhjsugd88p0bpmartsglw2avvh UNIQUE (doctor_doctor_id);


--
-- Name: medical_details ukjfw0kuyf59dyr376rj5s0j3wj; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.medical_details
    ADD CONSTRAINT ukjfw0kuyf59dyr376rj5s0j3wj UNIQUE (sap_email);


--
-- Name: password_change ukjsbeueu60782bufgxkxig6mv2; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.password_change
    ADD CONSTRAINT ukjsbeueu60782bufgxkxig6mv2 UNIQUE (doctor_doctor_id);


--
-- Name: password_change ukk2c4gpvmncg9o3hho0cp0pvte; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.password_change
    ADD CONSTRAINT ukk2c4gpvmncg9o3hho0cp0pvte UNIQUE (patient_sap_email);


--
-- Name: conformation ukleanfbsuv6u73d3xslawlmj95; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.conformation
    ADD CONSTRAINT ukleanfbsuv6u73d3xslawlmj95 UNIQUE (ad_ad_email);


--
-- Name: doctor uklr3j4y6twpk17qwuydiqi3yuh; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.doctor
    ADD CONSTRAINT uklr3j4y6twpk17qwuydiqi3yuh UNIQUE (doctor_email);


--
-- Name: appointment uklylisbiyi1fkijal2ekvkkvy1; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT uklylisbiyi1fkijal2ekvkkvy1 UNIQUE (prescription_id);


--
-- Name: appointment_form uks4lvl2yiw0mauu66b0j2q4xfw; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment_form
    ADD CONSTRAINT uks4lvl2yiw0mauu66b0j2q4xfw UNIQUE (prev_appointment);


--
-- Name: password_change uktif2h6c77xuoq9aiqhorcssja; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.password_change
    ADD CONSTRAINT uktif2h6c77xuoq9aiqhorcssja UNIQUE (ad_ad_email);


--
-- Name: current_appointment uktnkgn1ifx3kb9heqhktfh2lfl; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.current_appointment
    ADD CONSTRAINT uktnkgn1ifx3kb9heqhktfh2lfl UNIQUE (sap_email);


--
-- Name: prescription_medicine fk11s7ya8th68fbynjbbj20ldsx; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prescription_medicine
    ADD CONSTRAINT fk11s7ya8th68fbynjbbj20ldsx FOREIGN KEY (medicine) REFERENCES public.stocks(stock_id);


--
-- Name: appointment fk2qxfq7calhysrwb3lu7xlry2p; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT fk2qxfq7calhysrwb3lu7xlry2p FOREIGN KEY (prescription_id) REFERENCES public.prescription(prescription_id);


--
-- Name: appointment_form fk3dgmxp1koxga0xx4t0fbwg7ft; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment_form
    ADD CONSTRAINT fk3dgmxp1koxga0xx4t0fbwg7ft FOREIGN KEY (pref_doctor_doctor_id) REFERENCES public.doctor(doctor_id);


--
-- Name: password_change fk3ghwo0qhpwss7dfg8g71ymuic; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.password_change
    ADD CONSTRAINT fk3ghwo0qhpwss7dfg8g71ymuic FOREIGN KEY (ad_ad_email) REFERENCES public.ad(ad_email);


--
-- Name: conformation fk3u409hd18cph2j39gachdig7b; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.conformation
    ADD CONSTRAINT fk3u409hd18cph2j39gachdig7b FOREIGN KEY (patient_sap_email) REFERENCES public.patient(sap_email);


--
-- Name: patient_medical_details_mapping fk3yp3r06wjc4kduoub0yebg2wk; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.patient_medical_details_mapping
    ADD CONSTRAINT fk3yp3r06wjc4kduoub0yebg2wk FOREIGN KEY (medical_details_id) REFERENCES public.medical_details(id);


--
-- Name: current_appointment fk50qu7k8trsv60skwsrcyfa6cu; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.current_appointment
    ADD CONSTRAINT fk50qu7k8trsv60skwsrcyfa6cu FOREIGN KEY (appointment_id) REFERENCES public.appointment(appointment_id);


--
-- Name: medical_details fk5g5pt2nwaqbch4qcgflw25xk4; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.medical_details
    ADD CONSTRAINT fk5g5pt2nwaqbch4qcgflw25xk4 FOREIGN KEY (sap_email) REFERENCES public.patient(sap_email);


--
-- Name: doctor fk5j1on4uqqnbx14dj2wtfbbpq0; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.doctor
    ADD CONSTRAINT fk5j1on4uqqnbx14dj2wtfbbpq0 FOREIGN KEY (location) REFERENCES public.locations(location_id);


--
-- Name: appointment fk6bmsyywf1uqohdbum3em25264; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT fk6bmsyywf1uqohdbum3em25264 FOREIGN KEY (location) REFERENCES public.locations(location_id);


--
-- Name: prescription fk80h893kyydnhdaypndaof6qbb; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prescription
    ADD CONSTRAINT fk80h893kyydnhdaypndaof6qbb FOREIGN KEY (doctor_email) REFERENCES public.doctor(doctor_email);


--
-- Name: ad_prescription fka94hkmgw3bcgs5kgs3ljmqb5r; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ad_prescription
    ADD CONSTRAINT fka94hkmgw3bcgs5kgs3ljmqb5r FOREIGN KEY (medicine_stock_id) REFERENCES public.stocks(stock_id);


--
-- Name: conformation fkc96tnvrnv07phj634brd1jybq; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.conformation
    ADD CONSTRAINT fkc96tnvrnv07phj634brd1jybq FOREIGN KEY (ad_ad_email) REFERENCES public.ad(ad_email);


--
-- Name: current_appointment fkc987dat0irdrd7wrutmv9w5t9; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.current_appointment
    ADD CONSTRAINT fkc987dat0irdrd7wrutmv9w5t9 FOREIGN KEY (doctor_email) REFERENCES public.doctor(doctor_email);


--
-- Name: password_change fkcytwp0r3u9imuhjo4n84qp6fb; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.password_change
    ADD CONSTRAINT fkcytwp0r3u9imuhjo4n84qp6fb FOREIGN KEY (doctor_doctor_id) REFERENCES public.doctor(doctor_id);


--
-- Name: appointment_form fkf5uoyoi5yod4rowjxymbq8lan; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment_form
    ADD CONSTRAINT fkf5uoyoi5yod4rowjxymbq8lan FOREIGN KEY (prev_appointment) REFERENCES public.appointment(appointment_id);


--
-- Name: appointment fkfjhwhbge09j2eq04xnqmihgoh; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT fkfjhwhbge09j2eq04xnqmihgoh FOREIGN KEY (apt_form) REFERENCES public.appointment_form(id);


--
-- Name: conformation fkfli3esvelpovo911v7gyqlh50; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.conformation
    ADD CONSTRAINT fkfli3esvelpovo911v7gyqlh50 FOREIGN KEY (doctor_doctor_id) REFERENCES public.doctor(doctor_id);


--
-- Name: appointment fkgc4ny3t8c0ox7cp5c0go9tqce; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT fkgc4ny3t8c0ox7cp5c0go9tqce FOREIGN KEY (doctor_email) REFERENCES public.doctor(doctor_email);


--
-- Name: patient_medical_details_mapping fkmdf5vvlsxn4j5prs6ycivayn1; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.patient_medical_details_mapping
    ADD CONSTRAINT fkmdf5vvlsxn4j5prs6ycivayn1 FOREIGN KEY (sap_email) REFERENCES public.patient(sap_email);


--
-- Name: ad_prescription fknvwi8veodjfp48i66jtnsq1j2; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ad_prescription
    ADD CONSTRAINT fknvwi8veodjfp48i66jtnsq1j2 FOREIGN KEY (ad_ad_email) REFERENCES public.ad(ad_email);


--
-- Name: stocks fko8ar0c8qwiohg27fquwan1tc3; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.stocks
    ADD CONSTRAINT fko8ar0c8qwiohg27fquwan1tc3 FOREIGN KEY (location) REFERENCES public.locations(location_id);


--
-- Name: prescription_medicine fkobfsdmdjkbn8t6vjll9e558kb; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prescription_medicine
    ADD CONSTRAINT fkobfsdmdjkbn8t6vjll9e558kb FOREIGN KEY (prescription) REFERENCES public.prescription(prescription_id);


--
-- Name: ad_prescription fkog4oehurfloo91ywn4fu5yj2p; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ad_prescription
    ADD CONSTRAINT fkog4oehurfloo91ywn4fu5yj2p FOREIGN KEY (patient_sap_email) REFERENCES public.patient(sap_email);


--
-- Name: current_appointment fkp58oxwdgnraqtc4882xc9fueu; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.current_appointment
    ADD CONSTRAINT fkp58oxwdgnraqtc4882xc9fueu FOREIGN KEY (sap_email) REFERENCES public.patient(sap_email);


--
-- Name: prescription fkpk0pgoao5293828afxsqew6ej; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prescription
    ADD CONSTRAINT fkpk0pgoao5293828afxsqew6ej FOREIGN KEY (sap_email) REFERENCES public.patient(sap_email);


--
-- Name: password_change fkqbeara85ima16qvjpwk5ym3tb; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.password_change
    ADD CONSTRAINT fkqbeara85ima16qvjpwk5ym3tb FOREIGN KEY (patient_sap_email) REFERENCES public.patient(sap_email);


--
-- Name: appointment fkqxcw4myaen3ivsrqbsveu43i; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT fkqxcw4myaen3ivsrqbsveu43i FOREIGN KEY (sap_email) REFERENCES public.patient(sap_email);


--
-- Name: ad fktfnp6bf84v3445xxx2lunmkkc; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ad
    ADD CONSTRAINT fktfnp6bf84v3445xxx2lunmkkc FOREIGN KEY (location) REFERENCES public.locations(location_id);


--
-- PostgreSQL database dump complete
--

