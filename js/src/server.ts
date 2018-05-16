/*
 * Copyright (C) 2018 Matus Zamborsky
 * This file is part of The ONT Detective.
 *
 * The ONT Detective is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ONT Detective is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with The ONT Detective.  If not, see <http://www.gnu.org/licenses/>.
 */

import express from 'express';
import bodyParser from 'body-parser';
import cors from 'cors';
import config from './config';

import { GeneratorController } from './controllers/generator.controller';
import { ValidatorController } from './controllers/validator.controller';

const app: express.Application = express();
const port: number = config.app.port;

app.use(cors());
app.use(bodyParser.text({limit: '1mb'}));
app.use('/validate', ValidatorController);
app.use('/generate', GeneratorController);

// Serve the application at the given port
app.listen(port, () => {
    // Success callback
    console.log(`Listening at http://localhost:${port}/`);
});
