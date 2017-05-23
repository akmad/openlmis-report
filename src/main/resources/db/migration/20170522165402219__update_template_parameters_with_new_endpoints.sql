UPDATE template_parameters SET selectExpression = REPLACE(selectExpression, 'geographicZones/search?levelNumber=3', 'districts');
UPDATE template_parameters SET selectExpression = REPLACE(selectExpression, '/api/', '/api/reports/');
