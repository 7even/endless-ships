import React from 'react';
import endlessShips from '../reducers';
import { createStore } from 'redux';
import { Provider } from 'react-redux';
import { Grid, Row, Col } from 'react-bootstrap';
import { BrowserRouter as Router, Route } from 'react-router-dom';

import Reference from './Reference';
import Navigation from './Navigation';
import ShipsList from './ShipsList';
import ShipPage from './ShipPage';
import OutfitsList from './OutfitsList';

import 'bootstrap/dist/css/bootstrap.css';
import './App.css';

const App = () => (
  <Provider store={createStore(endlessShips)}>
    <Grid fluid={true}>
      <Row>
        <Col lg={12}>
          <Reference>
            <Router>
              <div className="app">
                <Navigation />
                <Route exact={true} path="/" component={ShipsList} />
                <Route path="/Ships/:shipName" component={ShipPage} />
                <Route exact={true} path="/outfits" component={OutfitsList} />
              </div>
            </Router>
          </Reference>
        </Col>
      </Row>
    </Grid>
  </Provider>
);

export default App;
