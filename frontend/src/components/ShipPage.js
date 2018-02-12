import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Panel, Image } from 'react-bootstrap';
import { FormattedNumber, kebabCase } from '../common';
import './ShipPage.css';

const intersperse = (arr, sep) => {
  if (arr.length === 0) {
    return [];
  } else {
    return arr.slice(1).reduce((xs, x, idx) => xs.concat([sep(idx), x]), [arr[0]]);
  }
};

const ShipLicenses = ({ licenses }) => {
  if (licenses.length === 2) {
    return <p className="licenses">This ship requires {licenses[0]} and {licenses[1]} licenses.</p>;
  } else {
    return <p className="licenses">This ship requires a {licenses[0]} license.</p>;
  }
};

const imageURL = (ship) => {
  var filename;

  if (ship.name === "Shuttle") {
    // for some crazy reason Shuttle has different sprite filenames
    filename = "ship/shuttle=0.png";
  } else if (ship.sprite[1]) {
    filename = window.encodeURI(ship.sprite[0]) + "-0.png";
  } else {
    filename = window.encodeURI(ship.sprite[0]) + ".png";
  }

  // probably not a good idea to hotlink to github to the master branch
  return "https://raw.githubusercontent.com/endless-sky/endless-sky/master/images/" + filename;
};

const OutfitItem = ({ name, quantity }) => {
  if (quantity === 1) {
    return <li className="list-group-item">{name}</li>;
  } else {
    return (
      <li className="list-group-item">
        <span className="badge">{quantity}</span>
        {name}
      </li>
    );
  }
};

const ShipDescription = ({ description }) => (
  <Row>
    <Col md={12}>
      <div className="well">
        {intersperse(description, (index) => <span key={index}><br/><br/></span>)}
      </div>
    </Col>
  </Row>
);

const ShipPage = ({ ship }) => (
  <div className="app">
    <Row>
      <Col md={6}>
        <Panel>
          <Panel.Heading>{ship.name}</Panel.Heading>

          <Panel.Body>
            <div className="media">
              <div className="media-body">
                <ul>
                  <li>cost: <FormattedNumber number={ship.cost} /></li>
                  <li>shields: <FormattedNumber number={ship.shields} /></li>
                  <li>hull: <FormattedNumber number={ship.hull} /></li>
                  <li>mass: <FormattedNumber number={ship.mass} /></li>
                  <li>cargo space: <FormattedNumber number={ship.cargoSpace} /></li>
                  <li>required crew: <FormattedNumber number={ship.requiredCrew} /></li>
                  <li>bunks: <FormattedNumber number={ship.bunks} /></li>
                  <li>fuel capacity: <FormattedNumber number={ship.fuelCapacity} /></li>
                  <li>outfit space: <FormattedNumber number={ship.outfitSpace} /></li>
                  <li>weapon capacity: <FormattedNumber number={ship.weaponCapacity} /></li>
                  <li>engine capacity: <FormattedNumber number={ship.engineCapacity} /></li>
                  <li>guns: <FormattedNumber number={ship.guns} /></li>
                  <li>turrets: <FormattedNumber number={ship.turrets} /></li>
                  {ship.drones > 0 && <li>drones: <FormattedNumber number={ship.drones} /></li>}
                  {ship.fighters > 0 && <li>fighters: <FormattedNumber number={ship.fighters} /></li>}
                </ul>

                {ship.licenses.length > 0 && <ShipLicenses licenses={ship.licenses} />}
              </div>

              <div className="media-right">
                <Image src={imageURL(ship)} className="ship-sprite" />
              </div>
            </div>
          </Panel.Body>
        </Panel>
      </Col>

      <Col md={6}>
        <Panel>
          <Panel.Heading>Default outfits</Panel.Heading>

          <Panel.Body>
            <ul className="list-group">
              {ship.outfits.map(outfit => <OutfitItem key={outfit.name} {...outfit} />)}
            </ul>
          </Panel.Body>
        </Panel>
      </Col>
    </Row>

    {ship.description.length > 0 && <ShipDescription description={ship.description}/>}
  </div>
);

const mapStateToProps = (state, { match: { params: { shipName } } }) => {
  return {
    ship: state.ships.find(ship => kebabCase(ship.name) === shipName)
  };
};

export default connect(mapStateToProps)(ShipPage);
