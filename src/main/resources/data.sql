INSERT INTO meetingroom.meeting_room (active, capacity, hourly_fee, id, name) VALUES (true, 8, 8000.00, 1, 'meeting-room-A');
INSERT INTO meetingroom.meeting_room (active, capacity, hourly_fee, id, name) VALUES (true, 12, 12000.00, 2, 'meeting-room-B');
INSERT INTO meetingroom.meeting_room (active, capacity, hourly_fee, id, name) VALUES (true, 16, 16000.00, 3, 'meeting-room-C');
INSERT INTO meetingroom.meeting_room (active, capacity, hourly_fee, id, name) VALUES (true, 10, 10000.00, 4, 'meeting-room-D');
INSERT INTO meetingroom.meeting_room (active, capacity, hourly_fee, id, name) VALUES (false, 12, 6000.00, 5, 'meeting-room-E');

INSERT INTO meetingroom.user (id, email, name) VALUES (1, 'A@gmail.com', 'A');
INSERT INTO meetingroom.user (id, email, name) VALUES (2, 'B@daum.net', 'B');
INSERT INTO meetingroom.user (id, email, name) VALUES (3, 'C@naver.com', 'C');

INSERT INTO meetingroom.payment_provider (id, api_endpoint, api_key, payment_provider_type) VALUES (1, '/api/v1/A', '3f12c4c7-1d53-4c26-b7c7-5f8b02317a59', 'A');
INSERT INTO meetingroom.payment_provider (id, api_endpoint, api_key, payment_provider_type) VALUES (2, '/api/v1/B', '8a02bb8e-d01c-45e3-9de7-f3b2b70abdc2', 'B');
INSERT INTO meetingroom.payment_provider (id, api_endpoint, api_key, payment_provider_type) VALUES (3, '/api/v2/C', '29fbe9b6-3017-4960-a7bb-3f9f3e4961ad', 'C');