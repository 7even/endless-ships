import React from 'react';
import endlessShips from '../reducers';
import { createStore } from 'redux';
import { Provider } from 'react-redux';
import { Grid, Row, Col } from 'react-bootstrap';
import { Router, Route } from 'react-router-dom';

import Reference from './Reference';
import Navigation from './Navigation';
import ShipsList from './ShipsList';
import ShipPage from './ShipPage';
import OutfitsList from './OutfitsList';
import OutfitPage from './OutfitPage';

import 'bootstrap/dist/css/bootstrap.css';
import './App.css';

const App = ({ history }) => (
  <Provider store={createStore(endlessShips)}>
    <Grid>
      <Row>
        <Col lg={12}>
          <Reference>
            <Router history={history}>
              <div className="app">
                <Navigation />
                <Route exact={true} path="/" component={ShipsList} />
                <Route path="/Ships/:shipName/:modificationName?" component={ShipPage} />
                <Route exact={true} path="/outfits" component={OutfitsList} />
                <Route path="/outfits/:outfitName" component={OutfitPage} />
              </div>
            </Router>
          </Reference>
        </Col>
      </Row>
    </Grid>
  </Provider>
);

export default App;
