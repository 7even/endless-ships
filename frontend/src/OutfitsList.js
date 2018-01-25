import React, { Component } from 'react';
import { Link } from 'react-router-dom';

class OutfitsList extends Component {
  render() {
    return (
      <div className="app">
        <ul className="nav nav-tabs">
          <li role="presentation">
            <Link to="/">Ships</Link>
          </li>
          <li role="presentation" className="active">
            <Link to="/outfits">Outfits</Link>
          </li>
        </ul>

        <div className="top20">outfits will be here</div>
      </div>
    );
  }
}

export default OutfitsList;
